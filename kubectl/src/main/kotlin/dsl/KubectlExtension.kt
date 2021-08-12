package de.joshuagleitze.gradle.kubectl.dsl

import de.joshuagleitze.gradle.kubectl.KubectlVersion
import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubernetes.GradleInputNotation
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import de.joshuagleitze.stringnotation.LowerCamelCase
import de.joshuagleitze.stringnotation.fromNotation
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import java.io.File

public open class KubectlExtension(private val project: Project, objects: ObjectFactory) {
	/**
	 * The version of kubectl that should be used by all tasks.
	 */
	public val version: Property<KubectlRelease> = objects.property<KubectlRelease>().convention(KubectlVersion.V1)

	/**
	 * Registers a task to apply and a task to delete the kustomization in the [kustomizationDir].
	 */
	@JvmOverloads
	public fun kustomization(
		kustomizationDir: File,
		configuration: Action<in MultiClusterKustomizationDeclaration> = Action {}
	): NamedDomainObjectProvider<MultiClusterKustomizationDeclaration> =
		project.kubernetes.deployments.register(dirToName(kustomizationDir), MultiClusterKustomizationDeclaration::class, configuration)

	@JvmOverloads
	public fun kustomization(configuration: Action<in MultiClusterKustomizationDeclaration> = Action {}): NamedDomainObjectProvider<MultiClusterKustomizationDeclaration> =
		kustomization(project.projectDir).apply { configure(configuration) }

	@JvmOverloads
	public fun kustomization(
		kustomizationDir: Any,
		configuration: Action<in MultiClusterKustomizationDeclaration> = Action {}
	): NamedDomainObjectProvider<MultiClusterKustomizationDeclaration> = kustomization(project.file(kustomizationDir), configuration)

	private fun dirToName(dir: File) =
		if (dir == project.projectDir) ""
		else dir.relativeTo(project.projectDir).path.fromNotation(GradleInputNotation).toNotation(LowerCamelCase)

	public companion object {
		public const val NAME: String = "kubectl"

		/**
		 * Gives access to the [KubectlExtension] on a given project.
		 */
		public val Project.kubectl: KubectlExtension get() = extensions.getByType()

		internal fun register(target: Project) = target.extensions.create(NAME, KubectlExtension::class, target)
	}
}
