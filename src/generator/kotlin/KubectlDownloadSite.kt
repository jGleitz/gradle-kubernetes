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
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import java.io.Closeable
import java.security.MessageDigest

class KubectlDownloadSite(private val output: KubectlDownloadReport): Closeable {
	private val client = HttpClient(CIO) {
		expectSuccess = true
		install(Logging) {
			level = LogLevel.NONE
		}
	}

	suspend fun findExecutables(kubectlVersion: Version): KubectlRelease = supervisorScope {
		val linux = async { findExecutable(kubectlVersion, Linux) }
		val macOs = async { findExecutable(kubectlVersion, MacOs) }
		val windows = async { findExecutable(kubectlVersion, Windows) }
		KubectlRelease(kubectlVersion, linux.await(), macOs.await(), windows.await())
	}

	private suspend fun findExecutable(kubectlVersion: Version, os: OperatingSystem) = runCatching {
		coroutineScope {
			output.reportDownloadStart(kubectlVersion, os)

			val hashJob = async { getSha512Hash(kubectlVersion, os) }
			val (downloadUrl, computedHashHex) = downloadExecutable(kubectlVersion, os)
			val hash = hashJob.await()
			check(computedHashHex == hash) {
				"hashes do not match for $downloadUrl: expected '$hash', found '$computedHashHex'"
			}
			KubectlDistribution(os, downloadUrl, hash)
		}
	}
		.onSuccess { output.reportDownloadSuccess(kubectlVersion, os) }
		.getOrElse {
			val error = ExecutableCheckException(kubectlVersion, os, it)
			output.reportDownloadFailure(kubectlVersion, os, error.message)
			throw error
		}

	private suspend fun getSha512Hash(kubectlVersion: Version, os: OperatingSystem) =
		client.get<String>(KUBECTL_DOWNLOAD_URL(kubectlVersion, os) + ".sha512").trim()

	private suspend fun downloadExecutable(kubectlVersion: Version, os: OperatingSystem): Pair<String, String> {
		val downloadUrl = KUBECTL_DOWNLOAD_URL(kubectlVersion, os)
		val sha512Digest = MessageDigest.getInstance("SHA-512")
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
		private val KUBECTL_DOWNLOAD_URL = { version: Version, os: OperatingSystem ->
			"https://storage.googleapis.com/kubernetes-release" +
				"/release/${version.toLowercaseNotation()}/bin/${os.downloadName}/amd64/${os.binaryName("kubectl")}"
		}
	}

	class ExecutableCheckException(val version: Version, val operatingSystem: OperatingSystem, val requestException: Throwable):
		Exception(requestException) {
		override val message: String get() = "Cannot find the executable for $version on $operatingSystem:\n$reasonString"

		val reasonString get() = generateReason(indent = 1, throwable = requestException)

		companion object {
			private fun generateReason(indent: Int, throwable: Throwable): String =
				(arrayOf(throwable) + throwable.suppressed)
					.map { if (it is ExecutableCheckException) it.requestException else it }
					.joinToString(separator = "\n") { error ->
						"  ".repeat(indent) + "- ${error.message} (${error::class.simpleName})" +
							(error.cause?.let { cause -> "\n" + generateReason(indent + 1, cause) } ?: "")
					}
		}
	}

	override fun close() = client.close()
}
