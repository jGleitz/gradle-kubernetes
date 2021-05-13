package de.joshuagleitze.gradle.kubernetes.dsl

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubernetes.data.*
import de.joshuagleitze.test.GradleIntegrationTestProject.integrationTestProject
import de.joshuagleitze.test.forGradleTest
import de.joshuagleitze.test.gradle.output
import de.joshuagleitze.test.gradle.task
import de.joshuagleitze.test.gradle.wasSuccessful
import io.kotest.core.spec.style.DescribeSpec
import java.net.URI
import kotlin.io.path.writeText

class GroovyClusterConfigurationSpec : DescribeSpec({
	describe("cluster configuration") {
		timeout = forGradleTest()

		beforeEach { integrationTestProject.prepare() }

		describe("Groovy") {
			it("allows to configure the default cluster with a kubeconfig context") {
				integrationTestProject.projectDir.resolve("build.gradle").writeText(
					"""
					plugins {
						id 'de.joshuagleitze.kubernetes'
					}
					
					kubernetes {
						cluster {
							kubeconfigContext 'test'
						}
					}
					""".trimIndent() + logConnectionTask("")
				)

				expect(integrationTestProject.runGradle(":logConnection")) {
					task(":logConnection").wasSuccessful()
					output.contains(KubeconfigContext(contextName = "test").toString())
				}
			}

			it("allows to configure a named cluster with a kubeconfig cluster") {
				integrationTestProject.projectDir.resolve("build.gradle").writeText(
					"""
					plugins {
						id 'de.joshuagleitze.kubernetes'
					}
					
					kubernetes {
						cluster('test') {
							kubeconfigCluster 'testcluster'
						}
					}
					""".trimIndent() + logConnectionTask("test")
				)

				expect(integrationTestProject.runGradle(":logConnection")) {
					task(":logConnection").wasSuccessful()
					output.contains(KubernetesCluster(KubeconfigCluster("testcluster"), NoAuth).toString())
				}
			}

			it("allows to configure the default cluster with a kubeconfig cluster and kubeconfig user") {
				integrationTestProject.projectDir.resolve("build.gradle").writeText(
					"""
					plugins {
						id 'de.joshuagleitze.kubernetes'
					}
					
					kubernetes {
						cluster {
							kubeconfigCluster('testcluster') {
								auth = kubeconfigUser 'testuser'
							}
						}
					}
					""".trimIndent() + logConnectionTask("")
				)

				expect(integrationTestProject.runGradle(":logConnection")) {
					task(":logConnection").wasSuccessful()
					output.contains(KubernetesCluster(KubeconfigCluster("testcluster"), KubeconfigUser("testuser")).toString())
				}
			}

			it("allows to configure a named cluster with an api server, certificate authority and basic auth") {
				integrationTestProject.projectDir.resolve("build.gradle").writeText(
					"""
					plugins {
						id('de.joshuagleitze.kubernetes')
					}
					
					kubernetes {
						cluster('live') {
							apiServer('https://example.com') {
								certificateAuthority = file 'k8s/live-ca.cert'
								auth = basicAuth 'liveUser', 'livePassword'
							}
						}
					}
					""".trimIndent() + logConnectionTask("live")
				)

				expect(integrationTestProject.runGradle(":logConnection")) {
					task(":logConnection").wasSuccessful()
					output.contains(
						KubernetesCluster(
							KubernetesApiServer(
								URI("https", "example.com", null, null),
								integrationTestProject.projectDir.resolve("k8s/live-ca.cert")
							),
							BasicAuth("liveUser", "livePassword")
						).toString()
					)
				}
			}
		}
	}
})
