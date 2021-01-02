package de.joshuagleitze.gradle.kubectl.dsl

import de.joshuagleitze.gradle.kubectl.data.Selector
import de.joshuagleitze.gradle.kubectl.tasks.KubectlApplyTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlDeleteTask
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesClusterDeclaration
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import de.joshuagleitze.gradle.kubernetes.dsl.MultiClusterKubernetesDeployment
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import java.io.File
import javax.inject.Inject

public open class MultiClusterKustomizationDeclaration @Inject constructor(
	name: String,
	private val project: Project,
	objects: ObjectFactory
): MultiClusterKubernetesDeployment<KustomizationDeclaration>(name, KustomizationDeclaration::class, project.kubernetes.clusters, objects),
	KustomizationSpec {
	init {
		kustomizationDir(project.projectDir.resolve(name))
	}

	override fun createNewInstance(
		createInstance: (args: Array<Any>) -> KustomizationDeclaration,
		cluster: NamedDomainObjectProvider<KubernetesClusterDeclaration>
	): KustomizationDeclaration = createInstance(arrayOf(name, cluster, project.tasks))

	/**
	 * Sets the given [selector] on all deployments by calling [KustomizationDeclaration.pruneBy] on each of them.
	 */
	final override fun pruneBy(selector: Selector): Unit = forEachCluster { it.pruneBy(selector) }

	/**
	 * Sets the given [dir] as kustomization directory on all deployments by calling [KustomizationDeclaration.kustomizationDir] on each of
	 * them.
	 */
	final override fun kustomizationDir(dir: File): Unit = forEachCluster { it.kustomizationDir(dir) }

	/**
	 * Configures the [applyTask][KustomizationDeclaration.applyTask] of each deployment.
	 */
	public fun applyTask(action: Action<in KubectlApplyTask>): Unit = forEachCluster { it.applyTask.configure(action) }

	/**
	 * Configures the [deleteTask][KustomizationDeclaration.deleteTask] of each deployment.
	 */
	public fun deleteTask(action: Action<in KubectlDeleteTask>): Unit = forEachCluster { it.deleteTask.configure(action) }

	internal companion object {
		internal fun factory(project: Project) =
			{ name: String -> project.objects.newInstance<MultiClusterKustomizationDeclaration>(name, project) }
	}
}
