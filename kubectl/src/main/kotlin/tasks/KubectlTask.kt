package de.joshuagleitze.gradle.kubectl.tasks

import de.joshuagleitze.gradle.kubectl.action.KubectlAction
import de.joshuagleitze.gradle.kubectl.data.Arguments
import de.joshuagleitze.gradle.kubectl.data.KubectlSpec
import de.joshuagleitze.gradle.kubectl.data.generateKubectlArguments
import de.joshuagleitze.gradle.kubectl.fileProvider
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask.Companion.downloadKubectl
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableVerificationTask.Companion.verifyKubectl
import de.joshuagleitze.gradle.kubernetes.data.KubernetesClusterConnection
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkerExecutor

public abstract class KubectlTask(private val workerExecutor: WorkerExecutor): DefaultTask(), KubectlSpec {
	@OutputFile
	override val logFile: RegularFileProperty = project.objects.fileProperty()
		.convention(project.fileProvider { project.buildDir.resolve("reports/kubectl/${name}.log") })

	@Input
	override val cluster: Property<KubernetesClusterConnection> = project.objects.property()

	@InputFile
	override val kubectlExecutable: RegularFileProperty = project.objects.fileProperty()
		.convention(project.tasks.downloadKubectl.flatMap { it.targetFile })

	@Input
	override var groupUnchangedMessages: Boolean = true

	init {
		dependsOn(project.tasks.verifyKubectl)
	}

	protected fun runKubectl(arguments: Arguments) {
		require(cluster.isPresent) {
			"A kubectl task must have a ${::cluster.name}!"
		}
		workerExecutor.noIsolation().submit(KubectlAction::class) {
			it.executable.set(this@KubectlTask.kubectlExecutable)
			it.arguments.set(this@KubectlTask.cluster.map { finalCluster ->
				(arguments + finalCluster.generateKubectlArguments()).toList()
			})
			it.logFile.set(this@KubectlTask.logFile)
			it.groupUnchangedMessages.set(this@KubectlTask.groupUnchangedMessages)
		}
	}

	final override fun dependsOn(vararg paths: Any?): Task = super.dependsOn(*paths)
}
