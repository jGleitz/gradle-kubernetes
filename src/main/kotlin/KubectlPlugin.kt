package de.joshuagleitze.gradle.kubectl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.host

@Suppress("unused")
class KubectlPlugin: Plugin<Project> {
	override fun apply(target: Project) {
		val host = host()
		val extension = KubectlExtension.register(target)
		val downloadTask = KubectlDownloadTask.register(target, host)
		val verifyTask = KubectlVerifyTask.register(target, host)
		downloadTask.configure { finalizedBy(verifyTask) }
		extension.executable.fileProvider(downloadTask.map { it.outputs.files.singleFile })
	}

	companion object {
		private const val EXTENSION_NAME = "kubectl"
	}
}
