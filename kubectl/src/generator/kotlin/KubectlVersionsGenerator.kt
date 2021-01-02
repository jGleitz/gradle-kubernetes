package de.joshuagleitze.gradle.kubectl.generator

import de.joshuagleitze.gradle.kubectl.data.Version
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.runBlocking
import java.io.File

object KubectlVersionsGenerator {
	private const val parallelism = 4

	@JvmStatic
	fun main(args: Array<String>) {
		val targetDir = File(args[0])
		val compilerOutputDir = File(args[1])
		val preCheckedReleases = KubectlVersionsObjectReader(compilerOutputDir)

		runBlocking(Default) {
			KubectlDownloadReport(reportIn = this).use { output ->
				output.reportVersionsAsCached(preCheckedReleases.versions)

				flowUsing(GitHubRepository("kubernetes", "kubectl")) { it.listTags() }
					.mapNotNull { Version.parse(it.name) }
					.map { if (it.major == 0) it.copy(major = 1) else it }
					.onEach { output.reportVersion(it) }
					.buffer(GitHubApi.MAX_PAGE_SIZE + 1)
					.using(KubectlDownloadSite(output)) { kubectlDownloadSite ->
						map { version ->
							async {
								preCheckedReleases[version] ?: runCatching {
									kubectlDownloadSite.findExecutables(version)
								}.getOrNull()
							}
						}
							.buffer(parallelism - 2)
							.mapNotNull { it.await() }
					}
					.bufferedChunks(128)
					.runningReduce { last, new -> last + new }
					.collect { KubectlVersionsObjectGenerator.generateVersionsObject(it, targetDir) }
			}
		}
	}
}
