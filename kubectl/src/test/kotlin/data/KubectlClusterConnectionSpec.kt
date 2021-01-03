package de.joshuagleitze.gradle.kubectl.data

import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.asList
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
import de.joshuagleitze.test.describeType
import de.joshuagleitze.test.spek.testfiles.testFiles
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.lifecycle.CachingMode.SCOPE
import java.io.File
import java.net.URI
import java.nio.file.Path

object KubectlClusterConnectionSpec: Spek({
    val testFiles = testFiles()
    val certificate by memoized(SCOPE) { testFiles.createFile("certificate") }

	describeType<KubeconfigContext> {
		it("generates the --context option") {
			expect(KubeconfigContext("testname"))
				.feature(KubernetesClusterConnection::generateKubectlArguments).asList()
				.containsExactly("--context=testname")
		}
	}

	describeType<KubernetesCluster> {
		it("combines the options of its values") {
			expect(
				KubernetesCluster(
					KubernetesApiServer(URI.create("https://example.com"), certificate),
					BasicAuth("testuser", "testpassword")
				)
			)
				.feature(KubernetesClusterConnection::generateKubectlArguments).asList()
				.containsExactly(
					"--server=https://example.com",
					"--certificate-authority=$certificate",
					"--username=testuser",
					"--password=testpassword"
				)
		}
	}

	describeType<KubeconfigCluster> {
		it("generates the --cluster option") {
			expect(KubeconfigCluster("testname"))
				.feature(KubernetesApiServerOptions::generateKubectlArguments).asList()
				.containsExactly("--cluster=testname")
		}
	}

	describeType<KubernetesApiServer> {
		it("generates the --server and --certificate-authority options (from Path)") {
			expect(KubernetesApiServer(URI.create("https://example.com"), certificate))
				.feature(KubernetesApiServerOptions::generateKubectlArguments).asList()
				.containsExactly("--server=https://example.com", "--certificate-authority=$certificate")
		}

		it("generates the --server and --certificate-authority options (from File)") {
			expect(KubernetesApiServer(URI.create("https://example.com"), certificate.toFile()))
				.feature(KubernetesApiServerOptions::generateKubectlArguments).asList()
				.containsExactly("--server=https://example.com", "--certificate-authority=$certificate")
		}

		it("omits --certificate-authority if it is null (from Path)") {
			expect(KubernetesApiServer(URI.create("https://example.com"), null as Path?))
				.feature(KubernetesApiServerOptions::generateKubectlArguments).asList()
				.containsExactly("--server=https://example.com")
		}

		it("omits --certificate-authority if it is null (from File)") {
			expect(KubernetesApiServer(URI.create("https://example.com"), null as File?))
				.feature(KubernetesApiServerOptions::generateKubectlArguments).asList()
				.containsExactly("--server=https://example.com")
		}
	}

	describeType<KubeconfigUser> {
		it("generates the --user option") {
			expect(KubeconfigUser("testname"))
				.feature(KubernetesAuthOptions::generateKubectlArguments).asList()
				.containsExactly("--user=testname")
		}
	}

	describeType<BasicAuth> {
		it("generates the --username and --pasword options") {
			expect(BasicAuth("testuser", "testpassword"))
				.feature(KubernetesAuthOptions::generateKubectlArguments).asList()
				.containsExactly("--username=testuser", "--password=testpassword")
		}
	}

	describeType<NoAuth> {
		it("generates no options") {
			expect(NoAuth)
				.feature(KubernetesAuthOptions::generateKubectlArguments).asList()
				.isEmpty()
		}
	}
})
