package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.dsl.KubectlExtension
import de.joshuagleitze.gradle.kubectl.dsl.MultiClusterKustomizationDeclaration
import de.joshuagleitze.gradle.kubectl.tasks.DeployTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableVerificationTask
import de.joshuagleitze.gradle.kubectl.tasks.TeardownTask
import de.joshuagleitze.gradle.kubernetes.KubernetesPlugin
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.register

public class KubectlPlugin: Plugin<Project> {
	override fun apply(target: Project) {
		target.plugins.apply(KubernetesPlugin::class)
		val kubectlExtension = KubectlExtension.register(target)
		target.kubernetes.deployments.registerFactory(MultiClusterKustomizationDeclaration::class.java, MultiClusterKustomizationDeclaration.factory(target))

		val downloadTask = target.tasks.register<KubectlExecutableDownloadTask>(KubectlExecutableDownloadTask.NAME) {
			kubectlRelease.set(kubectlExtension.version)
		}
		val verifyTask = target.tasks.register<KubectlExecutableVerificationTask>(KubectlExecutableVerificationTask.NAME) {
			kubectlRelease.set(kubectlExtension.version)
		}
		downloadTask.configure { it.finalizedBy(verifyTask) }

		DeployTask.register(target)
		TeardownTask.register(target)
	}
}
