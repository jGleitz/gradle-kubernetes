package de.joshuagleitze.gradle.kubernetes.dsl

import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskContainer
import javax.inject.Inject

open class ExampleDeploymentA @Inject constructor(cluster: ClusterProvider, tasks: TaskContainer):
	DefaultKubernetesDeployment<Task, Task>(
		cluster, tasks,
		deployment = tasks.register("deploymentAFor${cluster.name.capitalize()}"),
		teardown = tasks.register("teardownAFor${cluster.name.capitalize()}")
	)

open class ExampleMultiClusterDeploymentA @Inject constructor(name: String, clusters: ClusterContainer, objects: ObjectFactory):
	MultiClusterKubernetesDeployment<ExampleDeploymentA>(name, ExampleDeploymentA::class, clusters, objects)

open class ExampleDeploymentB @Inject constructor(cluster: ClusterProvider, tasks: TaskContainer):
	DefaultKubernetesDeployment<Task, Task>(
		cluster, tasks,
		deployment = tasks.register("deploymentBFor${cluster.name.capitalize()}"),
		teardown = tasks.register("teardownBFor${cluster.name.capitalize()}")
	)

open class ExampleMultiClusterDeploymentB @Inject constructor(name: String, clusters: ClusterContainer, objects: ObjectFactory):
	MultiClusterKubernetesDeployment<ExampleDeploymentB>(name, ExampleDeploymentB::class, clusters, objects)
