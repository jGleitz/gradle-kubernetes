package de.joshuagleitze.gradle.kubectl.data

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

public interface KubectlApplySpec: KubectlSpec {
	public val kustomizationDir: DirectoryProperty
	public val pruneSelector: Property<Selector>
	public var waitForResourceDeletion: Boolean
}
