package de.joshuagleitze.gradle.kubernetes.data

import ch.tutteli.atrium.api.fluent.en_GB.messageContains
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.test.describeType
import org.spekframework.spek2.Spek

object KubernetesClusterConnectionSpec: Spek({
	describeType<KubeconfigContext> {
		it("rejects being created with an empty context name") {
			expect {
				KubeconfigContext("")
			}.toThrow<IllegalArgumentException>().messageContains("context name")
		}
	}

	describeType<KubeconfigCluster> {
		it("rejects being created with an empty cluster name") {
			expect {
				KubeconfigCluster("")
			}.toThrow<IllegalArgumentException>().messageContains("cluster name")
		}
	}

	describeType<KubeconfigUser> {
		it("rejects being created with an empty cluster name") {
			expect {
				KubeconfigUser("")
			}.toThrow<IllegalArgumentException>().messageContains("user name")
		}
	}

	describeType<BasicAuth> {
		it("rejects being created with an empty user name") {
			expect {
				BasicAuth("", "testpassword")
			}.toThrow<IllegalArgumentException>().messageContains("user name")
		}

		it("rejects being created with an empty password") {
			expect {
				BasicAuth("testuser", "")
			}.toThrow<IllegalArgumentException>().messageContains("password")
		}
	}
})
