package de.joshuagleitze.gradle.kubectl.dsl

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.KubectlVersion
import de.joshuagleitze.test.GradleIntegrationTestProject.integrationTestProject
import de.joshuagleitze.test.forGradleTest
import de.joshuagleitze.test.gradle.output
import de.joshuagleitze.test.gradle.task
import de.joshuagleitze.test.gradle.wasSuccessful
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.io.path.*

object VersionConfigurationSpec: Spek({
	describe("version configuration") {
		beforeEachTest(integrationTestProject::prepare)

		val logVersionTask =
			"""
			
			tasks.register("logVersion") {
				doFirst {
					println(kubectl.version.get())
				}
			}
			""".trimIndent()

		describe("Kotlin") {
			it("allows to set the kubectl version", timeout = forGradleTest()) {
				(integrationTestProject.projectDir / "build.gradle.kts").writeText(
					"""
					import ${KubectlVersion::class.qualifiedName}.V1_16_7
					
					plugins {
						id("de.joshuagleitze.kubectl")
					}
					
					kubectl {
						version.set(V1_16_7)
					}
					""".trimIndent() + logVersionTask
				)

				expect(integrationTestProject.runGradle(":logVersion")) {
					task(":logVersion").wasSuccessful()
					output.contains(KubectlVersion.V1_16_7.toString())
				}
			}
		}

		describe("Groovy") {
			it("allows to set the kubectl version", timeout = forGradleTest()) {
				(integrationTestProject.projectDir /"build.gradle").writeText(
					"""
					import static ${KubectlVersion::class.qualifiedName}.V1_16_7
					
					plugins {
						id 'de.joshuagleitze.kubectl'
					}
					
					kubectl {
						version = V1_16_7
					}
					""".trimIndent() + logVersionTask
				)

				expect(integrationTestProject.runGradle(":logVersion")) {
					task(":logVersion").wasSuccessful()
					output.contains(KubectlVersion.V1_16_7.toString())
				}
			}
		}
	}
})
