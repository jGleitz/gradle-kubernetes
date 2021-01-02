package de.joshuagleitze.gradle.kubernetes

import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class KubernetesPlugin: Plugin<Project> {
	override fun apply(target: Project) {
		val kubernetesExtension = KubernetesExtension.register(target)
		target.afterEvaluate { kubernetesExtension.deployments.forEach { it.afterProjectEvaluated() } }
	}
}
