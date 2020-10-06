package de.joshuagleitze.gradle.kubectl.generator

import de.joshuagleitze.gradle.kubectl.data.KubectlDistribution
import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Version
import de.joshuagleitze.gradle.kubectl.data.Windows
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import org.slf4j.LoggerFactory
import java.io.Closeable

class KubectlDownloadSite: Closeable {
	private val log = LoggerFactory.getLogger(this::class.java)
	private val client = HttpClient(CIO) {
		expectSuccess = true
		install(Logging) {
			level = LogLevel.NONE
		}
	}

	suspend fun findExecutables(kubectlVersion: Version): KubectlRelease = supervisorScope {
		val versionString = kubectlVersion.toLowercaseNotation()
		log.debug("checking {}", kubectlVersion)
		runCatching {
			val linux = async { findExecutable(versionString, Linux) }
			val macOs = async { findExecutable(versionString, MacOs) }
			val windows = async { findExecutable(versionString, Windows) }
			KubectlRelease(kubectlVersion, linux.await(), windows.await(), macOs.await())
		}.getOrElse { throw NoExecutablesException(kubectlVersion, it) }
	}

	private suspend fun findExecutable(kubectlVersion: String, os: OperatingSystem) = coroutineScope {
		val hash = async { getSha512Hash(kubectlVersion, os) }
		val downloadUrl = async { checkExecutableUrl(kubectlVersion, os) }
		KubectlDistribution(os, downloadUrl.await(), hash.await())
	}

	private suspend fun getSha512Hash(kubectlVersion: String, os: OperatingSystem) =
		client.get<String>(KUBECTL_DOWNLOAD_URL(kubectlVersion, os) + ".sha512").trim()

	private suspend fun checkExecutableUrl(kubectlVersion: String, os: OperatingSystem): String {
		val downloadUrl = KUBECTL_DOWNLOAD_URL(kubectlVersion, os)
		client.head<Unit>(downloadUrl)
		return downloadUrl
	}

	companion object {
		private val KUBECTL_DOWNLOAD_URL = { version: String, os: OperatingSystem ->
			"https://storage.googleapis.com/kubernetes-release/release/$version/bin/${os.downloadName}/amd64/${os.binaryName("kubectl")}"
		}
	}

	class NoExecutablesException(val version: Version, val requestException: Throwable): Exception(requestException) {
		override val message: String
			get() = "Cannot find the executables for $version:\n" +
				(arrayOf(requestException) + requestException.suppressed)
					.joinToString(separator = "\n") {
						"    - ${it.message}"
					}
	}

	override fun close() = client.close()
}
