package de.joshuagleitze.gradle.kubectl.tasks

import de.joshuagleitze.gradle.kubectl.data.Arguments
import de.joshuagleitze.gradle.kubectl.data.KubectlDeleteSpec
import de.joshuagleitze.gradle.kubectl.data.Selector
import de.joshuagleitze.gradle.kubectl.tasks.TeardownTask.TEARDOWN_TASK_GROUP
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

public open class KubectlDeleteTask @Inject constructor(workerExecutor: WorkerExecutor): KubectlTask(workerExecutor), KubectlDeleteSpec {
	init {
		group = TEARDOWN_TASK_GROUP
		description = "removes resources from a kubernetes cluster"
	}

	@Optional
	@InputDirectory
	override val kustomizationDir: DirectoryProperty = project.objects.directoryProperty()

	@Optional
	@Input
	override val selector: Property<Selector> = project.objects.property()

	@Input
	override var ignoreNotFound: Boolean = true

	@Internal
	override var waitForResourceDeletion: Boolean = false

	@TaskAction
	public fun delete() {
		require(kustomizationDir.isPresent || selector.isPresent) {
			"A delete task must have a ${::selector.name} or a ${::kustomizationDir.name} (or both) to delete!"
		}

		runKubectl(
			Arguments("delete")
				.addIfPresent(kustomizationDir) { "--kustomize=${it.asFile.absolutePath}" }
				.addAllIfPresent(selector) { it.generateKubectlArguments() }
				.addIf(ignoreNotFound) { "--ignore-not-found" }
				.addIf(waitForResourceDeletion) { "--wait" }
		)
	}
}
