package de.joshuagleitze.gradle.kubectl.tasks

import de.joshuagleitze.gradle.kubectl.data.Arguments
import de.joshuagleitze.gradle.kubectl.data.KubectlApplySpec
import de.joshuagleitze.gradle.kubectl.data.Selector
import de.joshuagleitze.gradle.kubectl.tasks.DeployTask.DEPLOYMENT_TASK_GROUP
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

public open class KubectlApplyTask @Inject constructor(workerExecutor: WorkerExecutor): KubectlTask(workerExecutor), KubectlApplySpec {
	init {
		group = DEPLOYMENT_TASK_GROUP
		description = "deploys a resources to a kubernetes cluster"
	}

	@InputDirectory
	override val kustomizationDir: DirectoryProperty = project.objects.directoryProperty()

	@Optional
	@Input
	override val pruneSelector: Property<Selector> = project.objects.property<Selector>().convention(null as Selector?)

	@Internal
	override var waitForResourceDeletion: Boolean = false

	@TaskAction
	public fun apply() {
		runKubectl(
			Arguments("apply")
				.add("--kustomize=${kustomizationDir.asFile.get().absolutePath}")
				.addAllIfPresent(pruneSelector) { Arguments("--prune") + it.generateKubectlArguments() }
				.addIf(waitForResourceDeletion) { "--wait" }
		)
	}
}
