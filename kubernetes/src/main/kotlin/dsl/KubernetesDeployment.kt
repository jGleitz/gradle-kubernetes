package de.joshuagleitze.gradle.kubernetes.dsl

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Task

public interface KubernetesDeployment<DeployTask: Task, TeardownTask: Task>:
	DeployAndTeardownDeclaration<DeployTask, TeardownTask>, Named {
	public val cluster: ClusterProvider
}
