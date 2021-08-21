package de.joshuagleitze.gradle.kubernetes.data

import java.io.File
import java.io.Serializable
import java.net.URI
import java.nio.file.Path

public sealed class KubernetesClusterConnection : Serializable

public sealed class KubernetesApiServerOptions : Serializable

public sealed class KubernetesAuthOptions : Serializable

public data class KubeconfigContext(val contextName: String) : KubernetesClusterConnection() {
	init {
		require(contextName.isNotEmpty()) {
			"A kubeconfig context name must no be empty!"
		}
	}
}

public data class KubernetesCluster(val apiServer: KubernetesApiServerOptions, val auth: KubernetesAuthOptions) :
	KubernetesClusterConnection()

public data class KubeconfigCluster(val clusterName: String) : KubernetesApiServerOptions() {
	init {
		require(clusterName.isNotEmpty()) {
			"A kubeconfig cluster name must not be empty!"
		}
	}
}

public data class KubernetesApiServer(val url: URI, val certificateAuthority: Path?) : KubernetesApiServerOptions() {
	public constructor(url: URI, certificateAuthority: File?) : this(url, certificateAuthority?.toPath())
}

public data class KubeconfigUser(val userName: String) : KubernetesAuthOptions() {
	init {
		require(userName.isNotEmpty()) {
			"A kubeconfig user name must not be empty!"
		}
	}
}

public data class BasicAuth(val userName: String, val password: String) : KubernetesAuthOptions() {
	init {
		require(userName.isNotEmpty()) {
			"A kubernetes basic auth user name must not be empty!"
		}
		require(password.isNotEmpty()) {
			"A kubernetes basic auth password must not be empty!"
		}
	}
}

public data class MtlsAuth(val clientCertificate: Path, val clientKey: Path) : KubernetesAuthOptions() {
	public constructor(clientCertificate: File, clientKey: File) : this(clientCertificate.toPath(), clientKey.toPath())
}

public object NoAuth : KubernetesAuthOptions() {
	override fun toString(): String = "NoAuth"
}
