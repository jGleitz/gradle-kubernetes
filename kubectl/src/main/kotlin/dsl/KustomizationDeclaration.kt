package de.joshuagleitze.gradle.kubectl.dsl

import de.joshuagleitze.gradle.kubectl.data.Selector
import de.joshuagleitze.gradle.kubectl.tasks.KubectlApplyTask
import de.joshuagleitze.gradle.kubectl.tasks.KubectlDeleteTask
import de.joshuagleitze.gradle.kubernetes.dsl.ClusterProvider
import de.joshuagleitze.gradle.kubernetes.dsl.DefaultKubernetesDeployment
import de.joshuagleitze.stringnotation.LowerCamelCase
import de.joshuagleitze.stringnotation.fromNotation
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.inject.Inject

public open class KustomizationDeclaration @Inject constructor(
	containerName: String,
	cluster: ClusterProvider,
	tasks: TaskContainer
) : KustomizationSpec, DefaultKubernetesDeployment<KubectlApplyTask, KubectlDeleteTask>(
	cluster, tasks,
	deployment = tasks.register<KubectlApplyTask>(actionTaskName("apply", containerName, cluster)),
	teardown = tasks.register<KubectlDeleteTask>(actionTaskName("delete", containerName, cluster))
) {
	init {
		applyTask {
			this.cluster.set(this@KustomizationDeclaration.cluster.flatMap { it.connection })
		}
		deleteTask {
			this.cluster.set(this@KustomizationDeclaration.cluster.flatMap { it.connection })
		}
	}

	/**
	 * The task that will apply the kustomization. Synonym for [deployment].
	 */
	public val applyTask: TaskProvider<KubectlApplyTask> get() = deployment

	/**
	 * The task that will delete the kustomization. Synonym for [teardown].
	 */
	public val deleteTask: TaskProvider<KubectlDeleteTask> get() = teardown

	override fun kustomizationDir(dir: File) {
		applyTask {
			kustomizationDir.set(dir)
		}
		deleteTask {
			if (!selector.isPresent) kustomizationDir.set(dir)
		}
	}

	override fun pruneBy(selector: Selector) {
		applyTask {
			pruneSelector.set(selector)
		}
		deleteTask {
			kustomizationDir.set(null as Directory?)
			this.selector.set(selector)
		}
	}

	private companion object {
		private fun actionTaskName(action: String, containerName: String, cluster: ClusterProvider) = (
			action.fromNotation(LowerCamelCase)
				+ containerName.fromNotation(LowerCamelCase)
				+ "Kustomization"
				+ cluster.name.fromNotation(LowerCamelCase)
			).toNotation(LowerCamelCase)
	}
}
