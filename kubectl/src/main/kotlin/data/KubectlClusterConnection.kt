package de.joshuagleitze.gradle.kubectl.data

import de.joshuagleitze.gradle.kubectl.data.Arguments.Companion.noArguments
import de.joshuagleitze.gradle.kubernetes.data.*

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
	is MtlsAuth -> Arguments(
		"--client-certificate=${clientCertificate.toAbsolutePath()}",
		"--client-key=${clientKey.toAbsolutePath()}"
	)
}
