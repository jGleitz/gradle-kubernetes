package de.joshuagleitze.gradle.kubectl.data

import de.joshuagleitze.gradle.kubectl.data.Arguments.Companion.noArguments
import de.joshuagleitze.gradle.kubernetes.data.BasicAuth
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigCluster
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigContext
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigUser
import de.joshuagleitze.gradle.kubernetes.data.KubernetesApiServer
import de.joshuagleitze.gradle.kubernetes.data.KubernetesApiServerOptions
import de.joshuagleitze.gradle.kubernetes.data.KubernetesAuthOptions
import de.joshuagleitze.gradle.kubernetes.data.KubernetesCluster
import de.joshuagleitze.gradle.kubernetes.data.KubernetesClusterConnection
import de.joshuagleitze.gradle.kubernetes.data.NoAuth

internal fun KubernetesClusterConnection.generateKubectlArguments() = when (this) {
	is KubeconfigContext -> Arguments("--context=$contextName")
	is KubernetesCluster -> apiServer.generateKubectlArguments() + auth.generateKubectlArguments()
}

internal fun KubernetesApiServerOptions.generateKubectlArguments() = when (this) {
	is KubeconfigCluster -> Arguments("--cluster=$clusterName")
	is KubernetesApiServer -> Arguments("--server=$url")
		.addIfNotNull(certificateAuthority) { "--certificate-authority=${it.toAbsolutePath()}" }
}

internal fun KubernetesAuthOptions.generateKubectlArguments() = when (this) {
	is NoAuth -> noArguments()
	is KubeconfigUser -> Arguments("--user=$userName")
	is BasicAuth -> Arguments("--username=$userName", "--password=$password")
}
