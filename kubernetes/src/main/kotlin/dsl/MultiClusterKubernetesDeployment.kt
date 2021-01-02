package de.joshuagleitze.gradle.kubernetes.dsl

import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import kotlin.reflect.KClass

/**
 * A collection of the same logical deployment for different clusters. Gives access to each deployment for every known cluster.
 */
public abstract class MultiClusterKubernetesDeployment<ElementType: KubernetesDeployment<*, *>>(
	name: String,
	elementClass: KClass<ElementType>,
	private val clusters: ClusterContainer,
	objects: ObjectFactory
): Named {
	private val _name = name
	override fun getName(): String = _name

	private val elements: NamedDomainObjectContainer<ElementType> = objects.domainObjectContainer(elementClass.java) { clusterName ->
		createNewInstance({ objects.newInstance(elementClass, *it) }, clusters.named(clusterName))
	}.apply {
		addRule("${elementClass.simpleName} for existing clusters") {
			if (clusters.names.contains(it)) this@apply.register(it)
		}
	}

	/**
	 * Applies the given [action] to the deployment for the cluster with the given [name].
	 *
	 * @return a provider for the deployment for the cluster with the given [name].
	 */
	@JvmOverloads
	public fun forCluster(name: String, action: Action<in ElementType> = Action {}): NamedDomainObjectProvider<ElementType> =
		elements.named(name, action)

	/**
	 * Applies the given [action] to every deployment.
	 */
	public fun forEachCluster(action: Action<in ElementType>): Unit = elements.all(action)

	/**
	 * For every `multiClusterDeployment` in [multiClusterDeployments]: Makes each deployment in this multi cluster deployment
	 * [depend on][DeployAndTeardownDeclaration.dependsOn] the deployment in `multiClusterDeployment` with the same
	 * [cluster][KubernetesDeployment.cluster]. Requires that `multiClusterDeployment` has a deployment for every cluster that
	 * this multi cluster deployment has a deployment for.
	 */
	public fun dependsOn(vararg multiClusterDeployments: MultiClusterKubernetesDeployment<*>): Unit =
		dependsOn(multiClusterDeployments.asList())

	/**
	 * For every `multiClusterDeployment` in [multiClusterDeployments]: Makes each deployment in this multi cluster deployment
	 * [depend on][DeployAndTeardownDeclaration.dependsOn] the deployment in `multiClusterDeployment` with the same
	 * [cluster][KubernetesDeployment.cluster]. Requires that `multiClusterDeployment` has a deployment for every cluster that
	 * this multi cluster deployment has a deployment for.
	 */
	public fun dependsOn(multiClusterDeployments: Iterable<MultiClusterKubernetesDeployment<*>>): Unit = forEachCluster { ourClusterDeployment ->
		multiClusterDeployments.forEach { theirMultiClusterDeployment ->
			ourClusterDeployment.dependsOn(theirMultiClusterDeployment.forCluster(ourClusterDeployment.cluster.name))
		}
	}

	/**
	 * Makes each deployment in this multi cluster deployment [depend on][DeployAndTeardownDeclaration.dependsOn] the deployment in
	 * [multiClusterDeployment] with the same [cluster][KubernetesDeployment.cluster]. Requires that [multiClusterDeployment] has
	 * a deployment for every cluster that this multi cluster deployment has a deployment for.
	 */
	public fun dependsOn(multiClusterDeployment: MultiClusterKubernetesDeployment<*>): Unit = forEachCluster { ourClusterDeployment ->
		ourClusterDeployment.dependsOn(multiClusterDeployment.forCluster(ourClusterDeployment.cluster.name))
	}

	internal fun afterProjectEvaluated() {
		clusters.names.forEach { elements.named(it).get() }
	}

	protected open fun createNewInstance(
		createInstance: (args: Array<Any>) -> ElementType,
		cluster: NamedDomainObjectProvider<KubernetesClusterDeclaration>
	): ElementType = createInstance(arrayOf(cluster))

	override fun toString(): String = "multi-deployment '$name'"

	public companion object {
		public inline fun <reified T> defaultFactory(project: Project): (String) -> T = { name ->
			project.objects.newInstance(name, project.kubernetes.clusters)
		}
	}
}
