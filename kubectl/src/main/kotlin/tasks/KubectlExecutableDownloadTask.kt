package de.joshuagleitze.gradle.kubectl.tasks

import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.hostOperatingSystem
import de.undercouch.gradle.tasks.download.DownloadAction
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.property
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.host
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

public open class KubectlExecutableDownloadTask: DefaultTask() {
	@Input
	public val kubectlRelease: Property<KubectlRelease> = project.objects.property()

	init {
		timeout.convention(Duration.of(60, SECONDS))
	}

	@OutputFile
	public val targetFile: RegularFileProperty = project.objects.fileProperty()
		.convention(
			kubectlRelease.map { release ->
				val distribution = release[hostOperatingSystem()]
				project.gradle.gradleUserHomeDir
					.resolve(DOWNLOAD_TARGET_DIR)
					.resolve(distribution.name)
					.resolve(release.version.toLowercaseNotation())
					.resolve(distribution.executableName)
			}.let(project.layout::file)
		)

	@TaskAction
	public fun download() {
		checkHostPlatform()
		kubectlRelease.finalizeValue()
		val distribution = kubectlRelease.get()[hostOperatingSystem()]
		targetFile.finalizeValue()

		val action = createDownloadAction().apply {
			src(distribution.downloadUrl)
			dest(targetFile.asFile)
			readTimeout(timeout.get().toMillis().toInt())
			overwrite(false)
		}

		action.execute()

		if (action.isUpToDate) {
			state.didWork = false
		} else {
			targetFile.get().asFile.setExecutable(true, true)
		}
	}

	private fun checkHostPlatform() {
		val host = host()
		check(host.architecture.isAmd64) {
			"Cannot download kubectl for the architecture ${host.architecture}. Only amd64 is supported."
		}
	}

	public companion object {
		public const val NAME: String = "downloadKubectl"
		public const val DOWNLOAD_TARGET_DIR: String = "bin/kubectl"
		public val TaskContainer.downloadKubectl: TaskProvider<KubectlExecutableDownloadTask>
			get() = named<KubectlExecutableDownloadTask>(
				NAME
			)

		internal fun Task.createDownloadAction() = DownloadAction(project, this)
	}
}
