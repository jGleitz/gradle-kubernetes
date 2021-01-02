package de.joshuagleitze.gradle.kubectl

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.dsl.KubectlExtension
import de.joshuagleitze.gradle.kubectl.tasks.DeployTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableVerificationTask
import de.joshuagleitze.gradle.kubectl.tasks.TeardownTask
import de.joshuagleitze.gradle.kubernetes.KubernetesPlugin
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension
import de.joshuagleitze.test.GradleIntegrationTestProject.integrationTestProject
import de.joshuagleitze.test.describeType
import de.joshuagleitze.test.forGradleTest
import de.joshuagleitze.test.gradle.output
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import kotlin.io.path.*

object KubectlPluginSpec: Spek({
	val testProject by memoized {
		ProjectBuilder.builder().build()
			.also { it.plugins.apply(KubectlPlugin::class) }
	}

	describeType<KubectlPlugin> {
		it("registers the ${KubernetesExtension.NAME} plugin") {
			expect(testProject) {
				feature(Project::getPlugins)
					.feature(PluginContainer::findPlugin, KubernetesPlugin::class)
					.notToBeNull()
				feature(Project::getExtensions)
					.feature(ExtensionContainer::findByName, KubernetesExtension.NAME)
					.notToBeNull()
			}
		}

		it("registers the ${KubectlExtension.NAME} extension") {
			expect(testProject.extensions)
				.feature(ExtensionContainer::findByName, KubectlExtension.NAME)
				.isA<KubectlExtension>()
		}

		it("registers the ${KubectlExecutableDownloadTask.NAME} task") {
			expect(testProject.tasks)
				.feature(TaskContainer::findByPath, ":${KubectlExecutableDownloadTask.NAME}")
				.isA<KubectlExecutableDownloadTask>()
		}

		it("registers the ${KubectlExecutableVerificationTask.NAME} task") {
			expect(testProject.tasks)
				.feature(TaskContainer::findByPath, ":${KubectlExecutableVerificationTask.NAME}")
				.isA<KubectlExecutableVerificationTask>()
		}

		it("registers the ${DeployTask.NAME} task") {
			expect(testProject.tasks)
				.feature(TaskContainer::findByPath, ":${DeployTask.NAME}")
				.notToBeNull()
		}

		it("registers the ${TeardownTask.NAME} task") {
			expect(testProject.tasks)
				.feature(TaskContainer::findByPath, ":${TeardownTask.NAME}")
				.notToBeNull()
		}

		describe("gradle compatibility") {
			beforeEachTest(integrationTestProject::prepare)

			it("can be used together with the configuration cache", timeout = forGradleTest()) {
				(integrationTestProject.projectDir / "build.gradle.kts").writeText(
					"""
					plugins {
						id("de.joshuagleitze.kubectl")
					}
					
					kubernetes {
						cluster("dev") {
							kubeconfigContext("test-dev")
						}
						cluster("integration") {
							kubeconfigContext("test-integration")
						}
					}
					
					kubectl.kustomization {
						forEachCluster {
							kustomizationDir(file(cluster.name))
						}
					}
					""".trimIndent()
				)

				integrationTestProject.runGradle("deployDev", "teardownIntegration", "--dry-run", "--configuration-cache")
				val secondTime = integrationTestProject.runGradle("deployDev", "teardownIntegration", "--dry-run", "--configuration-cache")

				expect(secondTime)
					.output.contains("Reusing configuration cache")
			}
		}
	}
})
