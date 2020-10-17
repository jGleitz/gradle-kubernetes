package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.KubectlExtension.Companion.kubectlExtension
import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Windows
import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadTaskPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

object KubectlDownloadTask {
	const val NAME = "downloadKubectl"
	const val DOWNLOAD_TARGET_DIR = "bin/kubectl"
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

		val operatingSystem = determineOperatingSystem(platform)
		val distributionProvider = kubectlExtension.version.map { it[operatingSystem] }

		src(distributionProvider.map { it.downloadUrl })

		val targetFile = kubectlExtension.version.flatMap { release ->
			distributionProvider.map { distribution ->
				project.gradle.gradleUserHomeDir
					.resolve(DOWNLOAD_TARGET_DIR)
					.resolve(distribution.name)
					.resolve(release.version.toLowercaseNotation())
					.resolve(distribution.executableName)
			}
		}
		dest(targetFile)

		readTimeout(DEFAULT_DOWNLOAD_TIMEOUT.toMillis().toInt())
		overwrite(false)

		doLast {
			targetFile.get().setExecutable(true, true)
		}
	}

	internal fun determineOperatingSystem(platform: NativePlatformInternal): OperatingSystem {
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

	val TaskContainer.downloadKubectl get() = named(NAME)
}
