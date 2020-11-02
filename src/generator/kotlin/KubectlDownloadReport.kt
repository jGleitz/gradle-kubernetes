package de.joshuagleitze.gradle.kubectl.generator

import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Version
import de.joshuagleitze.gradle.kubectl.data.Windows
import de.joshuagleitze.gradle.kubectl.generator.KubectlDownloadReport.DownloadState.DOWNLOADING
import de.joshuagleitze.gradle.kubectl.generator.KubectlDownloadReport.DownloadState.FAILURE
import de.joshuagleitze.gradle.kubectl.generator.KubectlDownloadReport.DownloadState.SUCCESS
import de.joshuagleitze.gradle.kubectl.generator.KubectlDownloadReport.DownloadState.WAITING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Color.BLACK
import org.fusesource.jansi.Ansi.Color.CYAN
import org.fusesource.jansi.Ansi.Color.GREEN
import org.fusesource.jansi.Ansi.Color.RED
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import java.io.Closeable
import java.util.Comparator.comparing
import java.util.TreeMap
import java.util.TreeSet

class KubectlDownloadReport(reportIn: CoroutineScope): Closeable {
	private val versions = TreeMap<Version, ReleaseDownloadState>()
	private val errors = TreeSet(comparing<ErrorMessage, Version> { it.version }.thenComparing<String> { it.operatingSystem.name })
	private val updateChannel = Channel<() -> Unit>(5)

	init {
		AnsiConsole.systemInstall()
		reportIn.launch {
			for (update in updateChannel) {
				updateOutput(update)
			}
		}
	}

	override fun close() {
		if (errors.isEmpty()) {
			println(ansi().reset().fgGreen().a("Done with ${versions.size} versions.").reset())
		} else {
			println(ansi().reset().a("Finished ${versions.size} versions. ").fgYellow().a("Reported ${errors.size} errors!").reset())
		}
		updateChannel.close()
		AnsiConsole.systemUninstall()
	}

	suspend fun reportVersionsAsCached(versions: Iterable<Version>) = scheduleUpdate {
		versions.forEach { this.versions[it] = Cached }
	}

	suspend fun reportVersion(version: Version) = scheduleUpdate {
		versions.computeIfAbsent(version) { ReleaseDownloadStateData(WAITING, WAITING, WAITING) }
	}

	suspend fun reportDownloadStart(version: Version, operatingSystem: OperatingSystem) = reportState(version, operatingSystem, DOWNLOADING)
	suspend fun reportDownloadSuccess(version: Version, operatingSystem: OperatingSystem) = reportState(version, operatingSystem, SUCCESS)
	suspend fun reportDownloadFailure(version: Version, operatingSystem: OperatingSystem, reason: String) = scheduleUpdate {
		versions.getOrPut(version) { ReleaseDownloadStateData(WAITING, WAITING, WAITING) }[operatingSystem] = FAILURE
		errors += ErrorMessage(version, operatingSystem, reason)
	}

	private suspend fun reportState(version: Version, operatingSystem: OperatingSystem, state: DownloadState) = scheduleUpdate {
		versions.getOrPut(version) { ReleaseDownloadStateData(WAITING, WAITING, WAITING) }[operatingSystem] = state
	}

	private suspend fun scheduleUpdate(update: () -> Unit) = updateChannel.send(update)

	private fun updateOutput(update: () -> Unit) {
		val existingLineCount = versions.size + errors.sumBy { it.lineCount }
		update()
		print(
			ansi().cursorUpLine(existingLineCount)
				.appendAll(1..existingLineCount) { eraseLine().newline() }
				.cursorUpLine(existingLineCount)
				.appendAll(versions.entries) { (version, state) ->
					a(version.toLowercaseNotation().padStart(8)).a(": ").let(state::writeState).newline()
				}
				.appendAll(errors) {
					it.print(this).newline()
				}
		)
	}

	private interface ReleaseDownloadState {
		fun writeState(output: Ansi): Ansi

		operator fun get(operatingSystem: OperatingSystem): DownloadState
		operator fun set(operatingSystem: OperatingSystem, state: DownloadState)
	}

	private data class ReleaseDownloadStateData(var linux: DownloadState, var macOs: DownloadState, var windows: DownloadState):
		ReleaseDownloadState {
		override fun writeState(output: Ansi): Ansi = output.reset()
			.a("linux ").writeState(linux).a("   ")
			.a("macOs ").writeState(macOs).a("   ")
			.a("windows ").writeState(windows)

		private fun Ansi.writeState(state: DownloadState) = state.write(this)

		override fun get(operatingSystem: OperatingSystem) = when (operatingSystem) {
			is Linux -> linux
			is MacOs -> macOs
			is Windows -> windows
			else -> unknownOperatingSystem(operatingSystem)
		}

		override fun set(operatingSystem: OperatingSystem, state: DownloadState) {
			when (operatingSystem) {
				is Linux -> linux = state
				is MacOs -> macOs = state
				is Windows -> windows = state
				else -> unknownOperatingSystem(operatingSystem)
			}
		}

		private fun unknownOperatingSystem(operatingSystem: OperatingSystem): Nothing = error("I don’t know $operatingSystem!")
	}

	private object Cached: ReleaseDownloadState {
		private val delegate = ReleaseDownloadStateData(SUCCESS, SUCCESS, SUCCESS)
		override fun writeState(output: Ansi): Ansi = delegate.writeState(output).fgBright(BLACK).a(" (cached)").reset()

		override fun get(operatingSystem: OperatingSystem) = delegate[operatingSystem]

		override fun set(operatingSystem: OperatingSystem, state: DownloadState) =
			error("Cannot modify a cached download state!")
	}

	private class ErrorMessage(val version: Version, val operatingSystem: OperatingSystem, private val message: String) {
		val lineCount = message.count { it == '\n' } + 1
		fun print(output: Ansi) = output.reset().fgRed().a("⚠  ").a(message).reset()
	}

	private enum class DownloadState(val color: Ansi.Color, val symbol: Char) {
		WAITING(BLACK, '…'),
		DOWNLOADING(CYAN, '⇩'),
		FAILURE(RED, '✗'),
		SUCCESS(GREEN, '✓');

		fun write(output: Ansi) = output.reset().fg(color).a(symbol).reset()
	}

	private fun <T> Ansi.appendAll(elements: Iterable<T>, block: Ansi.(T) -> Ansi) = elements.fold(this) { last, element ->
		last.block(element)
	}
}
