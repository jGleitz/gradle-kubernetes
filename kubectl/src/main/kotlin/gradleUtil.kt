package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.OperatingSystem
import de.joshuagleitze.gradle.kubectl.data.Windows
import org.gradle.api.Project
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.io.File

internal fun hostOperatingSystem(): OperatingSystem {
	val os = DefaultNativePlatform.host().operatingSystem
	return when {
		os.isLinux -> Linux
		os.isMacOsX -> MacOs
		os.isWindows -> Windows
		else -> OperatingSystem(os.name)
	}
}


internal fun Project.fileProvider(providerFun: () -> File) = project.layout.file(project.provider(providerFun))
