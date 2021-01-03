package de.joshuagleitze.gradle.kubernetes

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension
import de.joshuagleitze.gradle.kubernetes.dsl.KubernetesExtension.Companion.kubernetes
import de.joshuagleitze.gradle.kubernetes.dsl.MultiClusterKubernetesDeployment
import de.joshuagleitze.test.describeType
import io.mockk.confirmVerified
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verifyAll
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import kotlin.time.seconds

object KubernetesPluginSpec: Spek({
	val testProject by memoized {
		(ProjectBuilder.builder().build() as ProjectInternal)
			.also { it.plugins.apply(KubernetesPlugin::class) }
	}
	describeType<KubernetesPlugin> {
		it("registers the ${KubernetesExtension.NAME} extension", timeout = 20.seconds.toLongMilliseconds() /* for CI */) {
			expect(testProject.extensions)
				.feature(ExtensionContainer::findByName, KubernetesExtension.NAME)
				.isA<KubernetesExtension>()
		}

		it(
			"calls ${MultiClusterKubernetesDeployment::class.simpleName}.${MultiClusterKubernetesDeployment<*>::afterProjectEvaluated.name} " +
				"after the project has been evaluated",
			timeout = 20.seconds.toLongMilliseconds() /* for CI */
		) {
			val mockDeployment = mockk<MultiClusterKubernetesDeployment<*>>(relaxed = true) {
				excludeRecords { name }
			}
			testProject.kubernetes.deployments.add(mockDeployment)
			confirmVerified(mockDeployment)

			testProject.evaluate()
			verifyAll {
				mockDeployment.afterProjectEvaluated()
			}
		}
	}
})
