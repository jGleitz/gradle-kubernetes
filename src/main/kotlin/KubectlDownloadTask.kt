package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.KubectlExtension.Companion.kubectlExtension
import de.joshuagleitze.gradle.kubectl.data.KubectlDistribution
import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Windows
import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadTaskPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

object KubectlDownloadTask {
	const val NAME = "downloadKubectl"
	private const val DOWNLOAD_TARGET_DIR = "kubectl"
	private val DEFAULT_DOWNLOAD_TIMEOUT = Duration.of(60, SECONDS)

	fun register(target: Project, platform: NativePlatformInternal): TaskProvider<out Task> {
		target.plugins.apply(DownloadTaskPlugin::class.java)
		return target.tasks.register(NAME, Download::class) {
			configureFor(platform, target.kubectlExtension)
		}
	}

	private fun Download.configureFor(platform: NativePlatformInternal, kubectlExtension: KubectlExtension) {
		check(platform.architecture.isAmd64) {
			"Cannot download kubectl for the architecture ${platform.architecture}. Only amd64 is supported."
		}

		val distributionProvider = kubectlExtension.kubectlVersion.map { it[determineOperatingSystem(platform)] }

		src(distributionProvider.map { it.downloadUrl })

		val targetFile = distributionProvider.map { distribution ->
			project.gradle.gradleUserHomeDir.resolve(DOWNLOAD_TARGET_DIR).resolve(distribution.name).resolve(distribution.executableName)
		}
		dest(targetFile)

		readTimeout(DEFAULT_DOWNLOAD_TIMEOUT.toMillis().toInt())

		doLast {
			targetFile.get().setExecutable(true, true)
		}
	}

	private fun determineOperatingSystem(platform: NativePlatformInternal): OperatingSystem {
		val os = platform.operatingSystem
		return when {
			os.isLinux -> Linux
			os.isMacOsX -> MacOs
			os.isWindows -> Windows
			else -> error(
				"Cannot download kubectl for the operating system $os. " +
					"Only Linux, macOs and Windows are supported!"
			)
		}
	}
}
