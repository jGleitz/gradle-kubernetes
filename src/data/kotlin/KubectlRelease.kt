package de.joshuagleitze.gradle.kubectl.data

data class KubectlRelease(
	val version: Version,
	val linux: KubectlDistribution,
	val macOs: KubectlDistribution,
	val windows: KubectlDistribution
) {
	operator fun get(operatingSystem: OperatingSystem) = when (operatingSystem) {
		is Linux -> linux
		is MacOs -> macOs
		is Windows -> windows
	}
}
