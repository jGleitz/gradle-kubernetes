package de.joshuagleitze.gradle.kubectl.data

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

/**
 * Configuration for a `kubectl delete` command.
 */
public interface KubectlDeleteSpec: KubectlSpec {
	/**
	 * If set, the resources defined by the kustomization in the given directory will be deleted. Corresponds to the `--kustomize` command
	 * line option of `kubectl delete`.
	 */
	public val kustomizationDir: DirectoryProperty

	/**
	 * If set, only resources matched by the given [Selector] will be deleted.
	 */
	public val selector: Property<Selector>
	public var ignoreNotFound: Boolean
	public var waitForResourceDeletion: Boolean
}
