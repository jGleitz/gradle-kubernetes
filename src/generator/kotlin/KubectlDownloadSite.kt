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
import io.ktor.utils.io.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.security.MessageDigest

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
		val hashJob = async { getSha512Hash(kubectlVersion, os) }
		val (downloadUrl, computedHashHex) = downloadExecutable(kubectlVersion, os)
		val hash = hashJob.await()
		log.debug("finished download from {}. expected hash: '{}', computedHash: '{}'", downloadUrl, hash, computedHashHex)
		check(computedHashHex == hash) {
			"hashes do not match for $downloadUrl: expected '$hash', found '$computedHashHex'"
		}
		log.info("verified {} for {}", kubectlVersion, os)
		KubectlDistribution(os, downloadUrl, hash)
	}

	private suspend fun getSha512Hash(kubectlVersion: String, os: OperatingSystem) =
		client.get<String>(KUBECTL_DOWNLOAD_URL(kubectlVersion, os) + ".sha512").trim()

	private suspend fun downloadExecutable(kubectlVersion: String, os: OperatingSystem): Pair<String, String> {
		val downloadUrl = KUBECTL_DOWNLOAD_URL(kubectlVersion, os)
		val sha512Digest = MessageDigest.getInstance("SHA-512")
		log.debug("Downloading executable from {}", downloadUrl)
		client.get<ByteReadChannel>(downloadUrl)
			.consumeEachBufferRange { buffer, _ ->
				sha512Digest.update(buffer)
				true
			}
			val hashHex = sha512Digest.digest()
			.joinToString(separator = "") { "%02x".format(it) }
		return downloadUrl to hashHex
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
