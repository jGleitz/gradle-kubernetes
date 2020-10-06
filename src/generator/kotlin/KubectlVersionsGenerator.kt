package de.joshuagleitze.gradle.kubectl.generator

import de.joshuagleitze.gradle.kubectl.data.Version
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File

object KubectlVersionsGenerator {
	private val parallelism = 16
	private val log = LoggerFactory.getLogger(this::class.java)

	@JvmStatic
	fun main(args: Array<String>) {
		val targetDir = File(args[0])
		val releasedExecutables = runBlocking {
			GitHubRepository("kubernetes", "kubectl").useForFlow { kubectlGitHubRepository ->
				KubectlDownloadSite().useForFlow { kubectlDownloadSite ->

					kubectlGitHubRepository.listTags()
						.mapNotNull { Version.parse(it.name) }
						.map { if (it.major == 0) it.copy(major = 1) else it }
						.map { version ->
							async {
								runCatching {
									kubectlDownloadSite.findExecutables(version)
								}
									.onFailure { log.error(it.message) }
									.getOrNull()
							}
						}
						.buffer(parallelism - 2)
						.mapNotNull { it.await() }
						.onEach { log.info("verified ${it.version}") }
						.flowOn(Dispatchers.IO)
				}
			}.toList()
		}

		KubectlVersionsObjectGenerator.generateVersionsObject(releasedExecutables, targetDir)
		log.info("Generated object with ${releasedExecutables.size} versions to $targetDir.")
	}
}
