package de.joshuagleitze.gradle.kubectl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.host

@Suppress("unused")
class KubectlPlugin: Plugin<Project> {
	override fun apply(target: Project) {
		KubectlExtension.register(target)
		KubectlDownloadTask.register(target, host())
	}

	companion object {
		private const val EXTENSION_NAME = "kubectl"
	}
}
