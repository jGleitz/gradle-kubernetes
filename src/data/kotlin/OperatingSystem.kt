package de.joshuagleitze.gradle.kubectl.data

sealed class OperatingSystem(val name: String, val downloadName: String) {
	abstract fun binaryName(baseName: String): String
	override fun toString() = name
}

object Linux: OperatingSystem("Linux", "linux") {
	override fun binaryName(baseName: String) = baseName
}

object MacOs: OperatingSystem("macOs", "darwin") {
	override fun binaryName(baseName: String) = baseName
}

object Windows: OperatingSystem("Windows", "windows") {
	override fun binaryName(baseName: String) = "$baseName.exe"
}
