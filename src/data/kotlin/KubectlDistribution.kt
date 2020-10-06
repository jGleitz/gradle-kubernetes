package de.joshuagleitze.gradle.kubectl.data

data class KubectlDistribution(val operatingSystem: OperatingSystem, val downloadUrl: String, val sha512Hash: String) {
	val name get() = operatingSystem.name
	val executableName get() = operatingSystem.binaryName("kubectl")
}
