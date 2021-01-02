package de.joshuagleitze.gradle.kubectl.tasks

import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.hostOperatingSystem
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask.Companion.downloadKubectl
import de.undercouch.gradle.tasks.download.VerifyAction
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.property

public open class KubectlExecutableVerificationTask: DefaultTask() {
	@Input
	public val kubectlRelease: Property<KubectlRelease> = project.objects.property()

	@InputFile
	public val executable: RegularFileProperty = project.objects.fileProperty()
		.convention(project.tasks.downloadKubectl.flatMap { it.targetFile })

	@TaskAction
	public fun verify() {
		kubectlRelease.finalizeValue()
		executable.finalizeValue()

		val action = createVerifyAction().apply {
			algorithm(HASH_ALGORITHM)
			src(executable.asFile)
			checksum(kubectlRelease.get()[hostOperatingSystem()].sha512Hash)
		}

		action.execute()
	}

	public companion object {
		public const val NAME: String = "verifyKubectl"
		public const val HASH_ALGORITHM: String = "SHA-512"
		public val TaskContainer.verifyKubectl: TaskProvider<KubectlExecutableVerificationTask>
			get() = named<KubectlExecutableVerificationTask>(
				NAME
			)

		internal fun Task.createVerifyAction() = VerifyAction(project)
	}
}
