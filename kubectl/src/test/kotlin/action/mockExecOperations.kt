package de.joshuagleitze.gradle.kubectl.action

import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Action
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.internal.DefaultExecSpec

fun mockExecOperations(execSpecTarget: MutableList<ExecSpec>): ExecOperations =
	mockk {
		every { exec(any()) } answersUsingExecSpec {
			execSpecTarget += this
		}
	}

infix fun MockKStubScope<ExecResult, ExecResult>.answersUsingExecSpec(specAction: ExecSpec.() -> Unit) = answers {
	val spec = DefaultExecSpec(IdentityFileResolver())
	firstArg<Action<in ExecSpec>>().execute(spec)
	specAction(spec)
	mockk()
}
