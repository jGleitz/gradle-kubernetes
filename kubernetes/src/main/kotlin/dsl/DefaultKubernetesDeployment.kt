package de.joshuagleitze.gradle.kubernetes.dsl

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

public abstract class DefaultKubernetesDeployment<DeployTask: Task, TeardownTask: Task>(
	final override val cluster: ClusterProvider,
	tasks: TaskContainer,
	override val deployment: TaskProvider<DeployTask>,
	override val teardown: TaskProvider<TeardownTask>
): KubernetesDeployment<DeployTask, TeardownTask> {
	init {
		tasks.clusterDeploymentTask(cluster).configure { it.dependsOn(deployment) }
		tasks.clusterTeardownTask(cluster).configure { it.dependsOn(teardown) }
	}

	final override fun getName(): String = cluster.name

	override fun toString(): String = "deployment '$name' for cluster '${cluster.name}'"
}
