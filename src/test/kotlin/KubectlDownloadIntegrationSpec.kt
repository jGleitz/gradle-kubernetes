import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.isReadable
import ch.tutteli.atrium.api.fluent.en_GB.resolve
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.core.polyfills.fullName
import de.joshuagleitze.gradle.kubectl.KubectlDownloadTask
import de.joshuagleitze.gradle.kubectl.KubectlVerifyTask
import de.joshuagleitze.gradle.kubectl.KubectlVersion
import de.joshuagleitze.test.gradle.failed
import de.joshuagleitze.test.gradle.output
import de.joshuagleitze.test.gradle.task
import de.joshuagleitze.test.gradle.wasInvoked
import de.joshuagleitze.test.gradle.wasSuccessful
import de.joshuagleitze.test.gradle.wasUpToDate
import de.joshuagleitze.test.spek.testfiles.testFiles
import org.gradle.testkit.runner.GradleRunner
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.lifecycle.CachingMode.SCOPE
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Files.copy
import java.nio.file.Files.move
import java.nio.file.Files.writeString
import java.nio.file.StandardCopyOption
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption
import java.nio.file.StandardOpenOption.APPEND
import kotlin.time.seconds

object KubectlDownloadIntegrationSpec: Spek({
	val testFiles = testFiles()
	val projectDir by memoized(SCOPE) { testFiles.createDirectory("testProject") }
	val gradleHome by memoized(SCOPE) { testFiles.createDirectory("gradleHome") }

	val osSystemProperty = System.getProperty("os.name").toLowerCase()
	val executableName = if (osSystemProperty.contains("win")) "kubectl.exe" else "kubectl"
	val osName = when {
		osSystemProperty.contains("win") -> "Windows"
		osSystemProperty.contains("mac") -> "macOs"
		osSystemProperty.contains(Regex("nix|nux|aix")) -> "Linux"
		else -> error("Cannot determine your os from '$osSystemProperty'!")
	}

	beforeGroup {
		projectDir.resolve("build.gradle.kts").toFile().writeText(
			"""
			import ${KubectlVersion::class.fullName}.V1_18_6
			plugins {
				id("de.joshuagleitze.kubectl")
			}
			
			kubectl {
				version.set(V1_18_6)
			}
			""".trimIndent()
		)
		projectDir.resolve("settings.gradle.kts").toFile().writeText(
			"""
			rootProject.name = "testProject"
			""".trimIndent()
		)
		// ensure that the gradle home is created for the group
		gradleHome.toFile()
	}

	fun runGradle(vararg arguments: String, expectFailure: Boolean = false) = GradleRunner.create()
		.withPluginClasspath()
		.forwardOutput()
		.withProjectDir(projectDir.toFile())
		.withTestKitDir(gradleHome.toAbsolutePath().toFile())
		.withArguments(*arguments, "--stacktrace")
		.run {
			if (expectFailure) buildAndFail()
			else build()
		}

	describe("download task") {
		it("downloads kubectl to <gradle home>/bin/kubectl/<os>/<version>", timeout = 60.seconds.toLongMilliseconds()) {
			expect(runGradle(KubectlDownloadTask.NAME)) {
				task(":${KubectlDownloadTask.NAME}").wasSuccessful()
				output.contains(executableName)
			}

			// TODO: check that it is executable!
			expect(gradleHome)
				.resolve("${KubectlDownloadTask.DOWNLOAD_TARGET_DIR}/$osName/v1.18.6/$executableName")
				.isReadable()
		}

		it("is up to date after being executed", timeout = 60.seconds.toLongMilliseconds()) {
			expect(runGradle(KubectlDownloadTask.NAME)) {
				task(":${KubectlDownloadTask.NAME}").wasInvoked()
			}

			expect(runGradle(KubectlDownloadTask.NAME)) {
				task(":${KubectlDownloadTask.NAME}").wasUpToDate()
			}
		}
	}

	describe("verify task") {
		it("is executed and succeeds after downloading", timeout = 60.seconds.toLongMilliseconds()) {
			expect(runGradle(KubectlDownloadTask.NAME)) {
				task(":${KubectlVerifyTask.NAME}").wasSuccessful()
			}
		}

		it("fails when changing the downloaded file", timeout = 60.seconds.toLongMilliseconds()) {
			expect(runGradle(KubectlDownloadTask.NAME)) {
				task(":${KubectlVerifyTask.NAME}").wasSuccessful()
			}

			val targetFile = gradleHome.resolve("${KubectlDownloadTask.DOWNLOAD_TARGET_DIR}/$osName/v1.18.6/$executableName")
			val backupFile = targetFile.resolveSibling("${targetFile.fileName}.bkp")
			copy(targetFile, backupFile)
			writeString(targetFile, "appended", APPEND)

			try {
				expect(runGradle(KubectlVerifyTask.NAME, expectFailure = true)) {
					task(":${KubectlVerifyTask.NAME}").failed()
				}
			} finally {
				move(backupFile, targetFile, REPLACE_EXISTING)
			}
		}
	}
})
