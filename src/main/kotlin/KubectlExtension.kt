package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.property

open class KubectlExtension(objects: ObjectFactory) {
	val kubectlVersion = objects.property<KubectlRelease>().convention(DEFAULT_KUBECTL_VERSION)

	companion object {
		private const val NAME = "kubectl"
		val DEFAULT_KUBECTL_VERSION: KubectlRelease = KubectlVersion.V1

		fun register(target: Project) = target.extensions.create(NAME, KubectlExtension::class)

		val Project.kubectlExtension get() = extensions.getByName<KubectlExtension>(NAME)
	}
}
