package de.joshuagleitze.gradle.kubectl.data

import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.messageContains
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.asList
import de.joshuagleitze.test.describeType
import org.spekframework.spek2.Spek

object SelectorSpec: Spek({
	describeType<LabelSelector> {
		it("generates the --selector option") {
			expect(LabelSelector("a" to "1", "b" to "2", "c" to "3"))
				.feature(Selector::generateKubectlArguments).asList()
				.containsExactly("--selector=a=1,b=2,c=3")
		}

		it("rejects being generated without any labels") {
			expect {
				LabelSelector()
			}.toThrow<IllegalArgumentException>().messageContains("one label")
		}

		it("rejects being generated with a label with an empty key") {
			expect {
				LabelSelector("a" to "1", "" to "2", "c" to "3")
			}.toThrow<IllegalArgumentException>().messageContains("'=2'")
		}
	}
})
