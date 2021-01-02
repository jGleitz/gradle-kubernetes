package de.joshuagleitze.gradle.kubectl.data

import de.joshuagleitze.gradle.kubernetes.data.KubernetesClusterConnection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import java.io.OutputStream

public interface KubectlSpec {
	public val cluster: Property<KubernetesClusterConnection>
	public val logFile: RegularFileProperty
	public val kubectlExecutable: RegularFileProperty
	public var groupUnchangedMessages: Boolean
}
