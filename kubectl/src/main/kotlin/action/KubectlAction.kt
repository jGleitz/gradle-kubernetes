package de.joshuagleitze.gradle.kubectl.action

import de.joshuagleitze.gradle.kubectl.action.KubectlAction.Parameters
import de.joshuagleitze.gradle.kubectl.output.DelegatingOutputStream.Companion.blockClose
import de.joshuagleitze.gradle.kubectl.output.DelegatingOutputStream.Companion.closeOnce
import de.joshuagleitze.gradle.kubectl.output.DelegatingOutputStream.Companion.onClose
import de.joshuagleitze.gradle.kubectl.output.LineFilter
import org.apache.commons.io.output.TeeOutputStream
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.Closeable
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

public abstract class KubectlAction @Inject constructor(private val execOperations: ExecOperations): WorkAction<Parameters> {
	public interface Parameters: WorkParameters {
		public val executable: RegularFileProperty
		public val arguments: ListProperty<String>
		public val logFile: RegularFileProperty
		public val groupUnchangedMessages: Property<Boolean>
	}

	override fun execute() {
		createOutputStreams().use { (standardOutputStream, errorOutputStream) ->
			execOperations.exec {
				it.executable(parameters.executable.get().asFile)
				it.args(parameters.arguments.get())
				it.standardOutput = standardOutputStream
				it.errorOutput = errorOutputStream
			}
		}

	}

	private fun createOutputStreams(): Pair<OutputStream, OutputStream> {
		val effectiveLogFile = parameters.logFile.get().asFile
		effectiveLogFile.parentFile.mkdirs()
		val targetStandardOutput = this.standardOutput
		val effectiveStandardOutput =
			if (parameters.groupUnchangedMessages.get()) createLineFilterFor(targetStandardOutput)
			else targetStandardOutput
		val logFileOutput = FileOutputStream(effectiveLogFile)
		val effectiveErrorOutput = this.errorOutput
		return TeeOutputStream(effectiveStandardOutput, logFileOutput).closeOnce() to
			TeeOutputStream(effectiveErrorOutput, logFileOutput).closeOnce()
	}

	protected open val standardOutput: OutputStream get() = System.out.blockClose()
	protected open val errorOutput: OutputStream get() = System.err.blockClose()

	private companion object {
		private val UNCHANGED_PATTERN = Regex(".*unchanged\n")

		private inline fun <A: Closeable, B: Closeable, R> Pair<A, B>.use(block: (Pair<A, B>) -> R): R =
			first.use { second.use { block(this) } }

		private fun createLineFilterFor(targetOutput: OutputStream) =
			LineFilter(targetOutput) { !it.matches(UNCHANGED_PATTERN) }
				.onClose { lineFilter ->
					if (lineFilter.removedLinesCount > 0) {
						targetOutput.write("${lineFilter.removedLinesCount} unchanged${System.lineSeparator()}".toByteArray())
						targetOutput.flush()
					}
				}
	}
}
