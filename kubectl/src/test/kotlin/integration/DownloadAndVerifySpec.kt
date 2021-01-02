package de.joshuagleitze.gradle.kubectl.integration

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.isExecutable
import ch.tutteli.atrium.api.fluent.en_GB.resolve
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.core.polyfills.fullName
import de.joshuagleitze.gradle.kubectl.KubectlVersion
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask.Companion.DOWNLOAD_TARGET_DIR
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableVerificationTask
import de.joshuagleitze.test.GradleIntegrationTestProject.integrationTestProject
import de.joshuagleitze.test.clearIfExists
import de.joshuagleitze.test.forGradleTest
import de.joshuagleitze.test.gradle.failed
import de.joshuagleitze.test.gradle.output
import de.joshuagleitze.test.gradle.task
import de.joshuagleitze.test.gradle.wasInvoked
import de.joshuagleitze.test.gradle.wasSuccessful
import de.joshuagleitze.test.gradle.wasUpToDate
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Files.createDirectories
import java.nio.file.Files.move
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.APPEND
import kotlin.io.path.*

object DownloadAndVerifySpec: Spek({
	val osSystemProperty = System.getProperty("os.name").toLowerCase()
	val executableName = if (osSystemProperty.contains("win")) "kubectl.exe" else "kubectl"
	val osName = when {
		osSystemProperty.contains("win") -> "Windows"
		osSystemProperty.contains("mac") -> "macOs"
		osSystemProperty.contains(Regex("nix|nux|aix")) -> "Linux"
		else -> error("Cannot determine your os from '$osSystemProperty'!")
	}
	val binDir = integrationTestProject.gradleHome / "bin"
	val binBackupDir = binDir.resolveSibling("${binDir.fileName}.bkp")

	describe("download & verify") {
		beforeGroup {
			integrationTestProject.prepare()
			binBackupDir.clearIfExists()
			createDirectories(binDir)
			binDir.moveTo(binBackupDir)
			(integrationTestProject.projectDir /"build.gradle.kts").writeText(
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
		}

		afterGroup {
			binDir.clearIfExists()
			binBackupDir.moveTo(binDir)
		}

		describe("download task") {
			it("downloads kubectl to <gradle home>/bin/kubectl/<os>/<version>", timeout = forGradleTest()) {
				expect(integrationTestProject.runGradle(KubectlExecutableDownloadTask.NAME)) {
					task(":${KubectlExecutableDownloadTask.NAME}").wasSuccessful()
					output.contains(executableName)
				}

				expect(integrationTestProject.gradleHome)
					.resolve("$DOWNLOAD_TARGET_DIR/$osName/v1.18.6/$executableName")
					.isExecutable()
			}

			it("is up to date after being executed", timeout = forGradleTest()) {
				expect(integrationTestProject.runGradle(KubectlExecutableDownloadTask.NAME)) {
					task(":${KubectlExecutableDownloadTask.NAME}").wasInvoked()
				}

				expect(integrationTestProject.runGradle(KubectlExecutableDownloadTask.NAME)) {
					task(":${KubectlExecutableDownloadTask.NAME}").wasUpToDate()
				}
			}
		}

		describe("verify task") {
			it("is executed and succeeds after downloading", timeout = forGradleTest()) {
				expect(integrationTestProject.runGradle(KubectlExecutableDownloadTask.NAME)) {
					task(":${KubectlExecutableVerificationTask.NAME}").wasSuccessful()
				}
			}

			it("fails when changing the downloaded file", timeout = forGradleTest()) {
				expect(integrationTestProject.runGradle(KubectlExecutableDownloadTask.NAME)) {
					task(":${KubectlExecutableVerificationTask.NAME}").wasSuccessful()
				}

				val targetFile = integrationTestProject.gradleHome / DOWNLOAD_TARGET_DIR / osName / "v1.18.6" / executableName
				val backupFile = targetFile.resolveSibling("${targetFile.fileName}.bkp")
				targetFile.copyTo(backupFile, REPLACE_EXISTING)
				try {
					targetFile.appendText("appended")

					expect(integrationTestProject.runGradle(KubectlExecutableVerificationTask.NAME, expectFailure = true)) {
						task(":${KubectlExecutableVerificationTask.NAME}").failed()
					}
				} finally {
					backupFile.moveTo(targetFile, REPLACE_EXISTING)
				}
			}
		}
	}
})
