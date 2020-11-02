package de.joshuagleitze.gradle.kubectl.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import de.joshuagleitze.gradle.kubectl.data.KubectlDistribution
import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Version
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object KubectlVersionsObjectGenerator {
	internal val KubectlVersion = ClassName("de.joshuagleitze.gradle.kubectl", "KubectlVersion")

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
		is KubectlRelease -> printKubectlRelease(instance)
		is KubectlDistribution -> printDataInstance(instance, printArgumentNames = true, chopArguments = true)
		is Version -> printDataInstance(instance, printArgumentNames = false, chopArguments = false)
		is OperatingSystem -> printObjectInstance(instance)
		else -> error("Cannot print ${instance::class}!")
	}

	private fun printKubectlRelease(release: KubectlRelease): CodeBlock {
		val distributionsCode = release.distributions.foldIndexed(CodeBlock.builder()) { index, code, distribution ->
			code.add(printInstance(distribution))
				.add(if (index < release.distributions.lastIndex) "," else "")
				.add("\n")
		}.build()

		return CodeBlock.builder()
			.add("%T(\n", KubectlRelease::class)
			.indent()
			.add(printInstance(release.version))
			.add(",\n")
			.add(distributionsCode)
			.unindent()
			.add(")")
			.build()
	}

	private fun printDataInstance(instance: Any, printArgumentNames: Boolean, chopArguments: Boolean): CodeBlock {
		require(instance::class.isData) {
			"Expected an instance of a data class, received an instance of ${instance::class}!"
		}
		@Suppress("UNCHECKED_CAST") val instanceClass = instance::class as (KClass<Any>)
		val openCode = CodeBlock.builder()
			.add("%T(", instance::class)
			.apply {
				if (chopArguments) {
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
				.add(if (printArgumentNames) "${parameter.name}·=·" else "")
				.add("%L", printInstance(parameterProperty(instance)))
				.add(if (index < constructorParameters.lastIndex) "," else "")
				.add(if (chopArguments) "\n" else if (index < constructorParameters.lastIndex) " " else "")
		}
		return propertiesCode
			.apply { if (chopArguments) unindent() }
			.add(")")
			.build()
	}

	private fun printObjectInstance(instance: Any): CodeBlock {
		require(instance::class.objectInstance != null) {
			"Expected an instance of an object, received an instance of ${instance::class}!"
		}
		return CodeBlock.of("%T", instance::class)
	}
}
