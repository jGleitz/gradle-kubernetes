package de.joshuagleitze.gradle.kubectl.dsl

import de.joshuagleitze.gradle.kubectl.data.Selector
import java.io.File

public interface KustomizationSpec {
	/**
	 * Configures pruning by the given [selector]. That means that when applying the kustomization, all resources that match the [selector],
	 * and are not part of the kustomization, will be deleted. When deleting, the kustomization will be ignored altogether
	 * ([KubectlDeleteSpec.kustomizationDir] will be unset) and all resources matching the [selector] will be deleted.
	 */
	public fun pruneBy(selector: Selector)

	/**
	 * Configures the given [dir] as kustomization dir for both deployment and teardown.
	 */
	public fun kustomizationDir(dir: File)
}
