package de.joshuagleitze.gradle.kubectl.generator

import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.data.Version
import de.joshuagleitze.gradle.kubectl.generator.KubectlVersionsObjectGenerator.KubectlVersion
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

class KubectlVersionsObjectReader(compilerOutputDir: File): Map<Version, KubectlRelease> by readCompiledObject(compilerOutputDir) {
	companion object {
		private val log = LoggerFactory.getLogger(KubectlVersionsObjectReader::class.java)
		private fun readCompiledObject(compilerOutputDir: File): Map<Version, KubectlRelease> {
			log.debug("reading the existing object from {}", compilerOutputDir)
			@Suppress("UNCHECKED_CAST")
			val kubectlVersionClass = try {
				val cl: ClassLoader = URLClassLoader(arrayOf(compilerOutputDir.toURI().toURL()))
				cl.loadClass(KubectlVersion.reflectionName())
			} catch (e: ClassNotFoundException) {
				log.info("found no pre-existing kubectl versions in {}", compilerOutputDir)
				return emptyMap()
			}.kotlin as KClass<Any>

			val kubectlVersionObject = kubectlVersionClass.objectInstance
				?: error("The $KubectlVersion read from $compilerOutputDir was not an object!")

			return kubectlVersionClass.memberProperties
				.filter { it.returnType == KubectlRelease::class.createType() }
				.map { it.get(kubectlVersionObject) as KubectlRelease }
				.associateBy { it.version }
				.also {
					log.info("Read {} pre-existing versions from {}", it.size, compilerOutputDir)
				}
		}
	}

	val versions get() = keys
}
