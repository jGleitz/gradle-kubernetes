package de.joshuagleitze.gradle.kubectl.integration

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.containsNot
import ch.tutteli.atrium.api.fluent.en_GB.exactly
import ch.tutteli.atrium.api.fluent.en_GB.value
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableVerificationTask
import de.joshuagleitze.test.GradleIntegrationTestProject.integrationTestProject
import de.joshuagleitze.test.forGradleTest
import de.joshuagleitze.test.forMinikubeTest
import de.joshuagleitze.test.gradle.output
import de.joshuagleitze.test.gradle.task
import de.joshuagleitze.test.gradle.wasSuccessful
import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import kotlin.io.path.*
import kotlin.reflect.KClass

val minikube = Minikube.use()

@EnabledIf(MinikubeTestsEnabled::class)
class KustomizationDeploymentSpec: DescribeSpec({
	timeout = forMinikubeTest() + forGradleTest()

	beforeContainer { minikube.awaitStart() }
	afterContainer { minikube.stop() }
	beforeEach { integrationTestProject.prepare() }

	describe("kustomization deployment") {
		fun setupKustomizationHelloWorldProject() {
			// example from https://github.com/kubernetes-sigs/kustomize/tree/master/examples/helloWorld
			with(integrationTestProject) {
				(projectDir / "build.gradle.kts").writeText(
					""" 
					plugins {
						id("de.joshuagleitze.kubectl")
					}
					
					kubernetes {
						cluster {
							kubeconfigContext("${Minikube.CONTEXT_NAME}")
						}
					}
					
					kubectl.kustomization(".")
					""".trimIndent()
				)
				(projectDir / "kustomization.yaml").writeText(
					"""
					# Example configuration for the webserver
					# at https://github.com/monopole/hello
					commonLabels:
					  app: hello
	
					resources:
					- deployment.yaml
					- service.yaml
					- configMap.yaml
					""".trimIndent()
				)
				(projectDir / "deployment.yaml").writeText(
					"""
					apiVersion: apps/v1
					kind: Deployment
					metadata:
					  name: the-deployment
					spec:
					  replicas: 3
					  selector:
					    matchLabels:
					      deployment: hello
					  template:
					    metadata:
					      labels:
					        deployment: hello
					    spec:
					      containers:
					      - name: the-container
					        image: monopole/hello:1
					        command: ["/hello",
					                  "--port=8080",
					                  "--enableRiskyFeature=${'$'}(ENABLE_RISKY)"]
					        ports:
					        - containerPort: 8080
					        env:
					        - name: ALT_GREETING
					          valueFrom:
					            configMapKeyRef:
					              name: the-map
					              key: altGreeting
					        - name: ENABLE_RISKY
					          valueFrom:
					            configMapKeyRef:
					              name: the-map
					              key: enableRisky
					""".trimIndent()
				)
				(projectDir / "service.yaml").writeText(
					"""
					kind: Service
					apiVersion: v1
					metadata:
					  name: the-service
					spec:
					  selector:
					    deployment: hello
					  type: LoadBalancer
					  ports:
					  - protocol: TCP
					    port: 8666
					    targetPort: 8080
					""".trimIndent()
				)
				(projectDir / "configMap.yaml").writeText(
					"""
					apiVersion: v1
					kind: ConfigMap
					metadata:
					  name: the-map
					data:
					  altGreeting: "Good Morning!"
					  enableRisky: "false"
					""".trimIndent()
				)
			}
		}

		it("deploys the kustomization hello world example") {
			setupKustomizationHelloWorldProject()
			val result = integrationTestProject.runGradle(":deploy")

			expect(result) {
				task(":${KubectlExecutableVerificationTask.NAME}").wasSuccessful()
				task(":applyKustomization").wasSuccessful()
				output.contains(
					"configmap/the-map created",
					"service/the-service created",
					"deployment.apps/the-deployment created"
				)
			}

			println("\n")

			expect(minikube.kubectl("get", "all")) {
				contains.exactly(1).value("deployment.apps/the-deployment")
				contains.exactly(1).value("service/the-service")
				// pods may not yet be available
				// contains.exactly(3).value("pod/the-deployment")
			}
		}

		it("tears down the kustomization hello world example") {
			setupKustomizationHelloWorldProject()
			integrationTestProject.runGradle(":deploy")

			val result = integrationTestProject.runGradle(":teardown")

			expect(result) {
				task(":deleteKustomization").wasSuccessful()
				output.contains(
					"""configmap "the-map" deleted""",
					"""service "the-service" deleted""",
					"""deployment.apps "the-deployment" deleted"""
				)
			}

			println("\n")

			expect(minikube.kubectl("get", "all")) {
				containsNot("deployment.apps/the-deployment")
				containsNot("service/the-service")
				// pods may still be in "terminating" state
				// containsNot("pod/the-deployment")
			}
		}
	}
})

class MinikubeTestsEnabled : EnabledCondition {
	override fun enabled(specKlass: KClass<out Spec>) = System.getenv("DISABLE_MINIKUBE_TEST") != "true"
}
