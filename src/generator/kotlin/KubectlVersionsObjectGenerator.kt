package de.joshuagleitze.gradle.kubectl.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import de.joshuagleitze.gradle.kubectl.data.KubectlDistribution
import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Version
import de.joshuagleitze.gradle.kubectl.data.Windows
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object KubectlVersionsObjectGenerator {
	private val KubectlVersion = ClassName("de.joshuagleitze.gradle.kubectl", "KubectlVersion")

	fun generateVersionsObject(executables: Iterable<KubectlRelease>, targetDir: File) {
		val enum = TypeSpec.objectBuilder(KubectlVersion)
			.addProperties(executables.map(::generateVersionProperty))
			.addProperties(generateShortcutProperties(executables))
			.build()

		FileSpec.builder(KubectlVersion.packageName, KubectlVersion.simpleName)
			.addType(enum)
			.build()
			.writeTo(targetDir)
	}

	private fun generateVersionProperty(release: KubectlRelease) =
		PropertySpec.builder(release.version.toScreamingSnakeCaseNotation(), KubectlRelease::class)
			.initializer(printInstance(release))
			.build()

	private fun generateShortcutProperties(releases: Iterable<KubectlRelease>): List<PropertySpec> {
		val sortedReleases = releases.sortedBy { it.version }
		val majors = sortedReleases.associateBy { "V${it.version.major}" }
		val minors = sortedReleases.associateBy { "V${it.version.major}_${it.version.minor}" }
		return (majors + minors).map { (shortcut, executables) ->
			PropertySpec.builder(shortcut, KubectlRelease::class)
				.initializer("%L", executables.version.toScreamingSnakeCaseNotation())
				.build()
		}
	}

	private fun printInstance(instance: Any?) = when (instance) {
		null -> CodeBlock.of("null")
		is Int -> CodeBlock.of("%L", instance)
		is String -> CodeBlock.of("%S", instance)
		is KubectlRelease -> printDataInstance(instance, printLong = true)
		is KubectlDistribution -> printDataInstance(instance, printLong = true)
		is Version -> printDataInstance(instance, printLong = false)
		is OperatingSystem -> when (instance) {
			is Linux -> CodeBlock.of("%T", Linux::class)
			is MacOs -> CodeBlock.of("%T", MacOs::class)
			is Windows -> CodeBlock.of("%T", Windows::class)
		}
		else -> error("Cannot print ${instance::class}!")
	}

	private fun printDataInstance(instance: Any, printLong: Boolean): CodeBlock {
		require(instance::class.isData) {
			"Can only be used for instance of a data class (received ${instance::class})!"
		}
		@Suppress("UNCHECKED_CAST") val instanceClass = instance::class as (KClass<Any>)
		val openCode = CodeBlock.builder()
			.add("%T(", instance::class)
			.apply {
				if (printLong) {
					add("\n")
					indent()
				}
			}
		val instanceProperties = instanceClass.memberProperties.associateBy { it.name }
		val constructorParameters = instanceClass.primaryConstructor!!/* this is a data class*/.parameters
		val propertiesCode = constructorParameters.foldIndexed(openCode) { index, code, parameter ->
			val parameterProperty = instanceProperties[
				parameter.name ?: error("this parameter has no name: $parameter of ${instanceClass.primaryConstructor}")
			] ?: error("cannot find the property corresponding to parameter $parameter!")
			code
				.add(if (printLong) "${parameter.name}·=·" else "")
				.add("%L", printInstance(parameterProperty(instance)))
				.add(if (index < constructorParameters.lastIndex) "," else "")
				.add(if (printLong) "\n" else if (index < constructorParameters.lastIndex) " " else "")
		}
		return propertiesCode
			.apply { if (printLong) unindent() }
			.add(")")
			.build()
	}
}
