package de.joshuagleitze.gradle.kubernetes.dsl

import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubernetes.KubernetesPlugin
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import de.joshuagleitze.test.findByName
import de.joshuagleitze.test.tasks
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder

class ClusterConfigurationBehaviourSpec : DescribeSpec({
	isolationMode = IsolationMode.InstancePerLeaf
	val testProject = ProjectBuilder.builder().build().also { it.plugins.apply(KubernetesPlugin::class) }

	beforeEach {
		testProject.kubernetes.apply {
			cluster("dev") {
				it.kubeconfigContext("test-dev")
			}
			cluster("integration") {
				it.kubeconfigContext("test-integration")
			}
			cluster("production") {
				it.kubeconfigContext("test-production")
			}
		}
	}

	describe("cluster configuration") {
		describe("behaviour") {
			it("creates a deploy task for every cluster") {
				expect(testProject).tasks {
					findByName("deployDev").notToBeNull()
					findByName("deployIntegration").notToBeNull()
					findByName("deployProduction").notToBeNull()
				}
			}

			it("creates a teardown task for every cluster") {
				expect(testProject).tasks {
					findByName("teardownDev").notToBeNull()
					findByName("teardownIntegration").notToBeNull()
					findByName("teardownProduction").notToBeNull()
				}
			}
		}
	}
})
