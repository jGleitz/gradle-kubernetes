package de.joshuagleitze.gradle.kubernetes.dsl

import ch.tutteli.atrium.api.fluent.en_GB.any
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubernetes.KubernetesPlugin
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import de.joshuagleitze.test.dependencies
import de.joshuagleitze.test.getByName
import de.joshuagleitze.test.mustRunAfter
import de.joshuagleitze.test.name
import de.joshuagleitze.test.tasks
import de.joshuagleitze.testfiles.spek.testFiles
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DeploymentConfigurationSpec: Spek({
	val testFiles = testFiles()
	val testProject by memoized {
		(ProjectBuilder.builder().withProjectDir(testFiles.createDirectory("projectDir").toFile()).build() as ProjectInternal)
			.also {
				it.plugins.apply(KubernetesPlugin::class)
				it.kubernetes.apply {
					deployments.registerFactory(
						ExampleMultiClusterDeploymentA::class.java,
						MultiClusterKubernetesDeployment.defaultFactory(it)
					)
					deployments.registerFactory(
						ExampleMultiClusterDeploymentB::class.java,
						MultiClusterKubernetesDeployment.defaultFactory(it)
					)
				}
			}
	}

	describe("deployment configuration") {
		describe("deployment dependencies") {
			beforeEachTest {
				testProject.kubernetes.apply {
					cluster("development") {
						it.kubeconfigContext("test-dev")
					}
					cluster("integration") {
						it.kubeconfigContext("test-integration")
					}
					cluster("production") {
						it.kubeconfigContext("test-production")
					}
					deployments.register("a", ExampleMultiClusterDeploymentA::class)
					deployments.register("b", ExampleMultiClusterDeploymentB::class)
				}
			}

			it("creates dependencies between individual kustomizations (by provider)") {
				testProject.kubernetes.apply {
					deployments["b"].forCluster("development") {
						it.dependsOn(deployments["a"].forCluster("development"))
					}
				}
				testProject.evaluate()

				expect(testProject).tasks {
					getByName("deploymentBForDevelopment").dependencies.any { name.toBe("deploymentAForDevelopment") }
					getByName("deploymentAForDevelopment").dependencies.isEmpty()
					listOf("A", "B").forEach { deployment ->
						listOf("Integration", "Production").forEach { environment ->
							getByName("deployment${deployment}For$environment").dependencies.isEmpty()
						}
					}

					getByName("teardownAForDevelopment").mustRunAfter.any { name.toBe("teardownBForDevelopment") }
					getByName("teardownBForDevelopment").mustRunAfter.isEmpty()
					listOf("A", "B").forEach { deployment ->
						listOf("Integration", "Production").forEach { environment ->
							getByName("teardown${deployment}For$environment").mustRunAfter.isEmpty()
						}
					}
				}
			}

			it("creates dependencies between individual kustomizations (directly)") {
				testProject.kubernetes.apply {
					val aDeploymentDev = deployments["a"].forCluster("development").get()
					val bDeploymentDev = deployments["b"].forCluster("development").get()
					bDeploymentDev.dependsOn(aDeploymentDev)
				}
				testProject.evaluate()

				expect(testProject).tasks {
					getByName("deploymentBForDevelopment").dependencies.any { name.toBe("deploymentAForDevelopment") }
					getByName("deploymentAForDevelopment").dependencies.isEmpty()
					listOf("A", "B").forEach { deployment ->
						listOf("Integration", "Production").forEach { environment ->
							getByName("deployment${deployment}For$environment").dependencies.isEmpty()
						}
					}

					getByName("teardownAForDevelopment").mustRunAfter.any { name.toBe("teardownBForDevelopment") }
					getByName("teardownBForDevelopment").mustRunAfter.isEmpty()
					listOf("A", "B").forEach { deployment ->
						listOf("Integration", "Production").forEach { environment ->
							getByName("teardown${deployment}For$environment").mustRunAfter.isEmpty()
						}
					}
				}
			}

			it("creates dependencies between kustomization containers") {
				testProject.kubernetes.apply {
					deployments["b"].dependsOn(deployments["a"])
				}
				testProject.evaluate()

				expect(testProject).tasks {
					listOf("Development", "Integration", "Production").forEach { environment ->
						getByName("deploymentBFor$environment").dependencies.any { name.toBe("deploymentAFor$environment") }
						getByName("deploymentAFor$environment").dependencies.isEmpty()

						getByName("teardownAFor$environment").mustRunAfter.any { name.toBe("teardownBFor$environment") }
						getByName("teardownBFor$environment").mustRunAfter.isEmpty()
					}
				}
			}

			it("makes the cluster deployment task depend on individual deployment tasks") {
				testProject.evaluate()

				expect(testProject).tasks {
					listOf("Development", "Integration", "Production").forEach { environment ->
						getByName("deploy$environment").dependencies.contains(
							{ name.toBe("deploymentAFor$environment") },
							{ name.toBe("deploymentBFor$environment") }
						)
					}
				}
			}

			it("makes the cluster teardown task depend on individual teardown tasks") {
				testProject.evaluate()

				expect(testProject).tasks {
					listOf("Development", "Integration", "Production").forEach { environment ->
						getByName("teardown$environment").dependencies.contains(
							{ name.toBe("teardownAFor$environment") },
							{ name.toBe("teardownBFor$environment") }
						)
					}
				}
			}
		}
	}
})
