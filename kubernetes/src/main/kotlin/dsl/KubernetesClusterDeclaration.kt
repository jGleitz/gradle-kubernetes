package de.joshuagleitze.gradle.kubernetes.dsl

import de.joshuagleitze.gradle.kubernetes.data.*
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import java.io.File
import java.net.URI
import javax.inject.Inject

public open class KubernetesClusterDeclaration(public val name: String, private val objects: ObjectFactory) {
	public val connection: Property<KubernetesClusterConnection> = objects.property()

	public fun kubeconfigContext(name: String): Unit = connection.set(KubeconfigContext(name))

	@JvmOverloads
	public fun kubeconfigCluster(name: String, authConfiguration: Action<in KubernetesAuthDeclaration> = Action {}) {
		val authConfig = objects.newInstance<KubernetesAuthDeclaration>()
		authConfiguration.execute(authConfig)
		connection.set(KubernetesCluster(KubeconfigCluster(name), authConfig.auth))
	}

	@JvmOverloads
	public fun apiServer(url: String, configuration: Action<in KubernetesApiServerDeclaration> = Action {}) {
		val config = objects.newInstance<KubernetesApiServerDeclaration>()
		configuration.execute(config)
		connection.set(
			KubernetesCluster(
				apiServer = KubernetesApiServer(URI.create(url), config.certificateAuthority),
				auth = config.auth
			)
		)
	}

	override fun toString(): String = "cluster '$name'"
}

public open class KubernetesAuthDeclaration @Inject constructor(private val objects: ObjectFactory) {
	public var auth: KubernetesAuthOptions = NoAuth
	public fun kubeconfigUser(name: String): KubeconfigUser = KubeconfigUser(name)
	public fun basicAuth(username: String, password: String): BasicAuth = BasicAuth(username, password)
	public fun mTLS(configuration: Action<in MtlsAuthDeclaration>): MtlsAuth = objects.newInstance<MtlsAuthDeclaration>()
		.apply { configuration.execute(this) }
		.build()
}

public open class MtlsAuthDeclaration {
	public var clientCertificate: File? = null
	public var clientKey: File? = null

	internal fun build(): MtlsAuth = MtlsAuth(
		requireNotNull(clientCertificate) { "A client certificate file is required for mTLS!" },
		requireNotNull(clientKey) { "A client key file is required for mTLS!" }
	)
}

public open class KubernetesApiServerDeclaration @Inject constructor(objects: ObjectFactory)
	: KubernetesAuthDeclaration(objects) {
	public var certificateAuthority: File? = null
}
