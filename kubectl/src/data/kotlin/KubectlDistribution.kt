package de.joshuagleitze.gradle.kubectl.data

import java.io.Serializable

public data class KubectlDistribution(val operatingSystem: OperatingSystem, val downloadUrl: String, val sha512Hash: String): Serializable {
	val name: String get() = operatingSystem.name
	val executableName: String get() = operatingSystem.binaryName("kubectl")
}
