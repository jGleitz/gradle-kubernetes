package de.joshuagleitze.gradle.kubernetes.dsl

import de.joshuagleitze.gradle.GradleInputNotation
import de.joshuagleitze.stringnotation.LowerCamelCase
import de.joshuagleitze.stringnotation.Word
import de.joshuagleitze.stringnotation.fromNotation
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Task
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

public class ClusterContainer(delegate: AbstractNamedDomainObjectContainer<KubernetesClusterDeclaration>, tasks: TaskContainer):
	NamedDomainObjectContainer<KubernetesClusterDeclaration> by delegate {
	init {
		delegate.whenElementKnown {
			tasks.registerClusterTasks(it.name)
		}
	}

	private fun TaskContainer.registerClusterTasks(clusterName: String) = maybeRegister(
		clusterTaskName(DEPLOY_TASK_ACTION, clusterName),
		clusterTaskName(TEARDOWN_TASK_ACTION, clusterName)
	)

	internal companion object {
		internal const val DEPLOY_TASK_ACTION = "deploy"
		internal const val TEARDOWN_TASK_ACTION = "teardown"
		internal fun clusterTaskName(action: String, clusterName: String) =
			(Word(action) + clusterName.fromNotation(GradleInputNotation)).toNotation(LowerCamelCase)

		private fun TaskContainer.maybeRegister(vararg taskNames: String) = (setOf(*taskNames) - this.names).forEach { register(it) }
	}
}

public typealias ClusterProvider = NamedDomainObjectProvider<KubernetesClusterDeclaration>

public fun TaskContainer.clusterDeploymentTask(cluster: ClusterProvider): TaskProvider<Task> =
	named(ClusterContainer.clusterTaskName(ClusterContainer.DEPLOY_TASK_ACTION, cluster.name))

public fun TaskContainer.clusterTeardownTask(cluster: ClusterProvider): TaskProvider<Task> =
	named(ClusterContainer.clusterTaskName(ClusterContainer.TEARDOWN_TASK_ACTION, cluster.name))
