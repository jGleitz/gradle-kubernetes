package de.joshuagleitze.gradle.kubernetes.dsl

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.invoke

/**
 * Captures the pair of tasks that are responsible for deploying and tearing down a set of resources.
 */
public interface DeployAndTeardownDeclaration<DeploymentTask: Task, TeardownTask: Task> {
	/**
	 * The task that will deploy this deployment’s resources.
	 */
	public val deployment: TaskProvider<DeploymentTask>

	/**
	 * The task that will un-deploy this deployment’s resources.
	 */
	public val teardown: TaskProvider<TeardownTask>

	/**
	 * Makes this [deployment][deployment] [depend on][Task.dependsOn] [dependency]’s [deployment] and forces [dependency]’s [teardown] to
	 * [run after][Task.mustRunAfter] this [teardown].
	 */
	public fun dependsOn(dependency: DeployAndTeardownDeclaration<*, *>) {
		this.deployment { dependsOn(dependency.deployment) }
		dependency.teardown { mustRunAfter(this@DeployAndTeardownDeclaration.teardown) }
	}

	/**
	 * Makes this [deployment][deployment] [depend on][Task.dependsOn] each of [dependencies]’ [deployment]s and forces each of
	 * [dependencies]’ [teardown]s to [run after][Task.mustRunAfter] this [teardown].
	 */
	public fun dependsOn(vararg dependencies: DeployAndTeardownDeclaration<*, *>): Unit = dependencies.forEach { dependsOn(it) }

	/**
	 * Makes this [deployment][deployment] [depend on][Task.dependsOn] [dependency]’s [deployment] and forces [dependency]’s [teardown] to
	 * [run after][Task.mustRunAfter] this [teardown].
	 */
	public fun dependsOn(dependency: NamedDomainObjectProvider<out DeployAndTeardownDeclaration<*, *>>) {
		this.deployment { dependsOn(dependency.map { it.deployment }) }
		dependency.configure { it.teardown { mustRunAfter(this@DeployAndTeardownDeclaration.teardown) } }
	}

	/**
	 * Makes this [deployment][deployment] [depend on][Task.dependsOn] [dependency]’s [deployment] and forces [dependency]’s [teardown] to
	 * [run after][Task.mustRunAfter] this [teardown].
	 */
	public fun dependsOn(vararg dependencies: NamedDomainObjectProvider<out DeployAndTeardownDeclaration<*, *>>): Unit =
		dependencies.forEach { dependsOn(it) }

}
