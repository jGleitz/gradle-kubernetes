package de.joshuagleitze.gradle.kubectl.dsl

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.KubectlPlugin
import de.joshuagleitze.gradle.kubectl.dsl.KubectlExtension.Companion.kubectl
import de.joshuagleitze.gradle.kubectl.tasks.KubectlApplyTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlDeleteTask
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigContext
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import de.joshuagleitze.test.findByName
import de.joshuagleitze.test.get
import de.joshuagleitze.test.getAsFile
import de.joshuagleitze.test.getAsPath
import de.joshuagleitze.testfiles.spek.testFiles
import de.joshuagleitze.test.tasks
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.io.path.*

object KustomizationConfigurationSpec: Spek({
	val testFiles = testFiles()
	val testProjectDir by memoized { testFiles.createDirectory("projectDir") }
	val testProject by memoized {
		(ProjectBuilder.builder().withProjectDir(testProjectDir.toFile()).build() as ProjectInternal)
			.also { it.plugins.apply(KubectlPlugin::class) }
	}

	describe("kustomization configuration") {
		it("adds a project folder kustomization for the default cluster") {
			testProject.kubernetes.cluster{
				it.kubeconfigContext("test")
			}
			testProject.kubectl.kustomization(".")

			testProject.evaluate()

			expect(testProject).tasks {
				findByName("applyKustomization").isA<KubectlApplyTask>()
					.feature(KubectlApplyTask::kustomizationDir).getAsPath()
					.toBe(testProjectDir)
				findByName("deleteKustomization").isA<KubectlDeleteTask>()
					.feature(KubectlDeleteTask::kustomizationDir).getAsPath()
					.toBe(testProjectDir)
			}
		}

		it("registers a kustomization in the ${KubernetesExtension.NAME}.${KubernetesExtension::deployments.name} container") {
			testProject.kubernetes.cluster {
				it.kubeconfigContext("test")
			}
			testProject.kubectl.kustomization("test")

			expect(testProject)
				.feature(Project::getExtensions)
				.feature(ExtensionContainer::getByName, KubernetesExtension.NAME).isA<KubernetesExtension>()
				.feature(KubernetesExtension::deployments)
				.feature(NamedDomainObjectContainer<out Any>::findByName, "test")
				.isA<MultiClusterKustomizationDeclaration>()
		}

		it("adds a kustomization variant for every cluster") {
			testProject.kubernetes.apply {
				cluster("dev") {
					it.kubeconfigContext("test-dev")
				}
				cluster("integration") {
					it.kubeconfigContext("test-integration")
				}
				cluster("production"){
					it.kubeconfigContext("test-production")
				}
			}
			testProject.kubectl.kustomization("multi") {
				it.forEachCluster {
					it.kustomizationDir(testProject.file("variants/${it.cluster.name}"))
				}
			}

			testProject.evaluate()

			expect(testProject).tasks {
				findByName("applyMultiKustomizationDev").isA<KubectlApplyTask> {
					feature(KubectlApplyTask::kustomizationDir).getAsPath()
						.toBe(testProjectDir / "variants" / "dev")
					feature(KubectlApplyTask::cluster).get().isA<KubeconfigContext>()
						.feature(KubeconfigContext::contextName)
						.toBe("test-dev")
				}
				findByName("deleteMultiKustomizationDev").isA<KubectlDeleteTask> {
					feature(KubectlDeleteTask::kustomizationDir).getAsPath()
						.toBe(testProjectDir / "variants" / "dev")
					feature(KubectlDeleteTask::cluster).get().isA<KubeconfigContext>()
						.feature(KubeconfigContext::contextName)
						.toBe("test-dev")
				}

				findByName("applyMultiKustomizationIntegration").isA<KubectlApplyTask> {
					feature(KubectlApplyTask::kustomizationDir).getAsPath()
						.toBe(testProjectDir / "variants" / "integration")
					feature(KubectlApplyTask::cluster).get().isA<KubeconfigContext>()
						.feature(KubeconfigContext::contextName)
						.toBe("test-integration")
				}
				findByName("deleteMultiKustomizationIntegration").isA<KubectlDeleteTask> {
					feature(KubectlDeleteTask::kustomizationDir).getAsPath()
						.toBe(testProjectDir / "variants" / "integration")
					feature(KubectlDeleteTask::cluster).get().isA<KubeconfigContext>()
						.feature(KubeconfigContext::contextName)
						.toBe("test-integration")
				}

				findByName("applyMultiKustomizationProduction").isA<KubectlApplyTask> {
					feature(KubectlApplyTask::kustomizationDir).getAsPath()
						.toBe(testProjectDir / "variants" / "production")
					feature(KubectlApplyTask::cluster).get().isA<KubeconfigContext>()
						.feature(KubeconfigContext::contextName)
						.toBe("test-production")
				}
				findByName("deleteMultiKustomizationProduction").isA<KubectlDeleteTask> {
					feature(KubectlDeleteTask::kustomizationDir).getAsPath()
						.toBe(testProjectDir / "variants" / "production")
					feature(KubectlDeleteTask::cluster).get().isA<KubeconfigContext>()
						.feature(KubeconfigContext::contextName)
						.toBe("test-production")
				}
			}
		}
	}
})
