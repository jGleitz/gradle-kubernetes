package de.joshuagleitze.gradle.kubernetes.dsl

import org.gradle.api.Action
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer

public open class KubernetesExtension(project: Project, objects: ObjectFactory) {
	/**
	 * All clusters that can be deployed to. All [deployments] will be configured for all clusters.
	 */
	public val clusters: ClusterContainer =
		ClusterContainer(
			objects.domainObjectContainer(KubernetesClusterDeclaration::class) as AbstractNamedDomainObjectContainer<KubernetesClusterDeclaration>,
			project.tasks
		)

	/**
	 * All deployments that will be deployed to each cluster. Use [MultiClusterKubernetesDeployment.forCluster] to configure deployment
	 * settings for individual clusters.
	 *
	 * If you want to create a new type of deployment but need to modify how it is created, use
	 * [ExtensiblePolymorphicDomainObjectContainer.registerFactory] to register a factory for your deployment type.
	 */
	public val deployments: ExtensiblePolymorphicDomainObjectContainer<MultiClusterKubernetesDeployment<*>> =
		objects.polymorphicDomainObjectContainer(MultiClusterKubernetesDeployment::class)

	/**
	 * Registers a cluster with the provided [name]. Equivalent to calling `clusters.register(name, configuration)`.
	 */
	@JvmOverloads
	public fun cluster(
		name: String = "",
		configuration: Action<in KubernetesClusterDeclaration>
	): NamedDomainObjectProvider<out KubernetesClusterDeclaration> = clusters.register(name, configuration)

	public companion object {
		public const val NAME: String = "kubernetes"

		/**
		 * Gives access to the [KubernetesExtension] on a given project.
		 */
		public val Project.kubernetes: KubernetesExtension get() = extensions.getByType()

		internal fun register(target: Project) = target.extensions.create(NAME, KubernetesExtension::class, target)
	}
}
