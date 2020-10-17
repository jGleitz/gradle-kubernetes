package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.KubectlDownloadTask.downloadKubectl
import de.joshuagleitze.gradle.kubectl.KubectlExtension.Companion.kubectlExtension
import de.undercouch.gradle.tasks.download.DownloadTaskPlugin
import de.undercouch.gradle.tasks.download.Verify
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal

object KubectlVerifyTask {
	const val NAME = "verifyKubectl"
	const val HASH_ALGORITHM = "SHA-512"

	fun register(target: Project, platform: NativePlatformInternal): TaskProvider<out Task> {
		target.plugins.apply(DownloadTaskPlugin::class.java)
		return target.tasks.register(NAME, Verify::class) {
			configureFor(platform, target.kubectlExtension, target.tasks.downloadKubectl)
		}
	}

	private fun Verify.configureFor(platform: NativePlatformInternal, kubectlExtension: KubectlExtension, downloadTask: TaskProvider<*>) {
		val distributionProvider = kubectlExtension.version.map { it[KubectlDownloadTask.determineOperatingSystem(platform)] }
		algorithm(HASH_ALGORITHM)
		src(downloadTask.map { it.outputs.files.singleFile })
		checksum(distributionProvider.get().sha512Hash)
	}
}
