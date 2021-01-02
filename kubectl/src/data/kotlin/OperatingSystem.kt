package de.joshuagleitze.gradle.kubectl.data

import java.io.Serializable

public open class OperatingSystem(public val name: String, public val downloadName: String): Serializable {
	public constructor(name: String): this(name, name)

	public open fun binaryName(baseName: String): String = baseName
	override fun toString(): String = name
}

public object Linux: OperatingSystem("Linux", "linux")

public object MacOs: OperatingSystem("macOs", "darwin")

public object Windows: OperatingSystem("Windows", "windows") {
	override fun binaryName(baseName: String): String = "$baseName.exe"
}
