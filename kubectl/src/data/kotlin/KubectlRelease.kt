package de.joshuagleitze.gradle.kubectl.data

import java.io.Serializable

public data class KubectlRelease(val version: Version, val distributions: List<KubectlDistribution>): Serializable {
	public constructor(version: Version, vararg distributions: KubectlDistribution): this(version, distributions.asList())

	public operator fun get(operatingSystem: OperatingSystem): KubectlDistribution = distributions.find { it.operatingSystem == operatingSystem }
		?: error("kubectl $version is not available for $operatingSystem! Only ${distributions.joinToString { it.name }} are supported.")
}
