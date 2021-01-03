package de.joshuagleitze.gradle.kubectl.action

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.containsNot
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isRegularFile
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.test.describeType
import de.joshuagleitze.test.instantiator
import de.joshuagleitze.test.spek.testfiles.testFiles
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.spyk
import io.mockk.verify
import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode.SCOPE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.util.LinkedList
import kotlin.io.path.*

object KubectlActionSpec: Spek({
	val testFiles = testFiles()
	val testProject by memoized(SCOPE) { ProjectBuilder.builder().build() }
	val reportsDir by memoized { testFiles.createDirectory("reports") }
	val createdExecSpecs by memoized(SCOPE) { LinkedList<ExecSpec>() }
	val mockExecOperations by memoized(SCOPE) { mockExecOperations(createdExecSpecs) }

	beforeEachTest { createdExecSpecs.clear() }

	fun testKubectlAction(
		standardOutput: OutputStream? = null,
		errorOutput: OutputStream? = null,
		configuration: KubectlAction.Parameters.() -> Unit = {}
	) = TestKubectlAction(
		testProject.instantiator.newInstance(KubectlAction.Parameters::class.java)
			.apply {
				executable.set(File("/test/executable"))
				arguments.set(emptyList())
				logFile.set((reportsDir / "standard.log").toFile())
				groupUnchangedMessages.set(true)
			}
			.apply(configuration),
		mockExecOperations,
		standardOutput,
		errorOutput
	)

	describeType<KubectlAction> {
		it("uses the configured executable") {
			val testExecutable = testFiles.createFile("special-executable")
			val testAction = testKubectlAction {
				executable.set(testExecutable.toFile())
			}
			testAction.execute()

			expect(createdExecSpecs.single())
				.feature(ExecSpec::getExecutable)
				.toBe(testExecutable.toString())
		}

		it("uses the configured arguments") {
			val testAction = testKubectlAction {
				arguments.set(listOf("one", "two", "three"))
			}
			testAction.execute()

			expect(createdExecSpecs.single())
				.feature(ExecSpec::getArgs)
				.containsExactly("one", "two", "three")
		}

		describe("output") {
			it("prints to the configured standardOutput") {
				val testOutput = ByteArrayOutputStream()
				val testAction = testKubectlAction(standardOutput = testOutput)
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					standardOutput.write("test output 42".toByteArray())
					standardOutput.flush()
				}
				testAction.execute()

				expect(testOutput.toString()).toBe("test output 42")
			}

			it("defaults the standard output to System.out") {
				val oldOut = System.out
				val testOutput = ByteArrayOutputStream()

				try {
					System.setOut(PrintStream(testOutput))

					val testAction = testKubectlAction()
					every { mockExecOperations.exec(any()) } answersUsingExecSpec {
						standardOutput.write("test output 42".toByteArray())
						standardOutput.flush()
					}
					testAction.execute()
				} finally {
					System.setOut(oldOut)
				}

				expect(testOutput.toString()).toBe("test output 42")
			}

			it("prints the standard output to the log file") {
				val targetLogFile = reportsDir / "kubectl" /"test.log"
				val testAction = testKubectlAction {
					logFile.set(targetLogFile.toFile())
				}
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					standardOutput.write("test output 42".toByteArray())
					standardOutput.flush()
				}
				testAction.execute()

				expect(targetLogFile) {
					isRegularFile()
					feature("content") { readText() }.toBe("test output 42")
				}
			}

			it("prints to the configured errorOutput") {
				val testOutput = ByteArrayOutputStream()
				val testAction = testKubectlAction(errorOutput = testOutput)
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					errorOutput.write("test output 42".toByteArray())
					errorOutput.flush()
				}
				testAction.execute()

				expect(testOutput.toString()).toBe("test output 42")
			}

			it("defaults the error output to System.err") {
				val oldErr = System.err
				val testOutput = ByteArrayOutputStream()

				try {
					System.setErr(PrintStream(testOutput))

					val testAction = testKubectlAction()
					every { mockExecOperations.exec(any()) } answersUsingExecSpec {
						errorOutput.write("test output 42".toByteArray())
						errorOutput.flush()
					}
					testAction.execute()
				} finally {
					System.setErr(oldErr)
				}

				expect(testOutput.toString()).toBe("test output 42")
			}

			it("prints the error output to the log file") {
				val targetLogFile = reportsDir / "kubectl" /"test.log"
				val testAction = testKubectlAction {
					logFile.set(targetLogFile.toFile())
				}
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					errorOutput.write("test output 42".toByteArray())
					errorOutput.flush()
				}
				testAction.execute()

				expect(targetLogFile) {
					isRegularFile()
					feature("content") { readText() }.toBe("test output 42")
				}
			}

			it("groups unchanged messages if requested") {
				val testOutput = ByteArrayOutputStream()
				val testAction = testKubectlAction(standardOutput = testOutput) {
					groupUnchangedMessages.set(true)
				}
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					standardOutput.write(
						"""
						resource 1 created
						resource 2 unchanged
						resource 3 unchanged
						resource 4 modified
						resource 5 unchanged
						resource 6 unchanged
						
						""".trimIndent().toByteArray()
					)
					standardOutput.flush()
				}
				testAction.execute()

				expect(testOutput.toString()) {
					contains("resource 1 created", "resource 4 modified")
					containsNot("resource 2", "resource 3", "resource 5", "resource 6")
					contains("4 unchanged")
				}
			}

			it("does not group unchanged messages if not requested") {
				val testOutput = ByteArrayOutputStream()
				val testAction = testKubectlAction(standardOutput = testOutput) {
					groupUnchangedMessages.set(false)
				}
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					standardOutput.write(
						"""
						resource 1 created
						resource 2 unchanged
						resource 3 unchanged
						resource 4 modified
						resource 5 unchanged
						resource 6 unchanged
						
						""".trimIndent().toByteArray()
					)
					standardOutput.flush()
				}
				testAction.execute()

				expect(testOutput.toString()) {
					contains(
						"resource 1 created",
						"resource 2 unchanged",
						"resource 3 unchanged",
						"resource 4 modified",
						"resource 5 unchanged",
						"resource 6 unchanged"
					)
				}
			}

			it("does not group unchanged messages in the log file") {
				val targetLogFile = reportsDir / "kubectl" / "test.log"
				val testAction = testKubectlAction {
					logFile.set(targetLogFile.toFile())
					groupUnchangedMessages.set(false)
				}
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					standardOutput.write(
						"""
						resource 1 created
						resource 2 unchanged
						resource 3 unchanged
						resource 4 modified
						resource 5 unchanged
						resource 6 unchanged
						
						""".trimIndent().toByteArray()
					)
					standardOutput.flush()
				}
				testAction.execute()

				expect(targetLogFile)
					.feature("content") { readText() }
					.contains(
						"resource 1 created",
						"resource 2 unchanged",
						"resource 3 unchanged",
						"resource 4 modified",
						"resource 5 unchanged",
						"resource 6 unchanged"
					)

			}

			it("closes the standardOutput stream") {
				val testStandardOutput = spyk(ByteArrayOutputStream())
				val testAction = testKubectlAction(standardOutput = testStandardOutput)
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					standardOutput.write("abc".toByteArray())
					standardOutput.flush()
				}
				testAction.execute()

				excludeRecords {
					testStandardOutput.write(any<ByteArray>())
					testStandardOutput.write(any(), any(), any())
					testStandardOutput.flush()
				}
				verify {
					testStandardOutput.close()
				}
					confirmVerified(testStandardOutput)
			}

			it("closes the errorOutput stream") {
				val testErrorOutput = spyk(ByteArrayOutputStream())
				val testAction = testKubectlAction(errorOutput = testErrorOutput)
				every { mockExecOperations.exec(any()) } answersUsingExecSpec {
					errorOutput.write("abc".toByteArray())
					errorOutput.flush()
				}
				testAction.execute()

				excludeRecords {
					testErrorOutput.write(any<ByteArray>())
					testErrorOutput.write(any(), any(), any())
					testErrorOutput.flush()
				}
				verify {
					testErrorOutput.close()
				}
				confirmVerified(testErrorOutput)
			}

			it("does not close the standardOutput when defaulting to System.out") {
				val oldOut = System.out
				val testStandardOutput = spyk(ByteArrayOutputStream())
				System.setOut(PrintStream(testStandardOutput))
				try {
					val testAction = testKubectlAction()
					every { mockExecOperations.exec(any()) } answersUsingExecSpec {
						standardOutput.write("abc".toByteArray())
						standardOutput.flush()
					}
					testAction.execute()
				} finally {
					System.setOut(oldOut)
				}

				excludeRecords {
					testStandardOutput.write(any<ByteArray>())
					testStandardOutput.write(any(), any(), any())
					testStandardOutput.flush()
				}
				confirmVerified(testStandardOutput)
			}

			it("does not close the errorOutput when defaulting to System.out") {
				val oldErr = System.err
				val testErrorOutput = spyk(ByteArrayOutputStream())
				System.setErr(PrintStream(testErrorOutput))
				try {
					val testAction = testKubectlAction()
					every { mockExecOperations.exec(any()) } answersUsingExecSpec {
						errorOutput.write("abc".toByteArray())
						errorOutput.flush()
					}
					testAction.execute()
				} finally {
					System.setErr(oldErr)
				}

				excludeRecords {
					testErrorOutput.write(any<ByteArray>())
					testErrorOutput.write(any(), any(), any())
					testErrorOutput.flush()
				}
				confirmVerified(testErrorOutput)
			}
		}
	}
}) {
	private class TestKubectlAction(
		private val parameters: Parameters,
		execOperations: ExecOperations,
		standardOutput: OutputStream?,
		errorOutput: OutputStream?
	): KubectlAction(execOperations) {
		override fun getParameters() = parameters
		override val standardOutput = standardOutput ?: super.standardOutput
		override val errorOutput = errorOutput ?: super.errorOutput
	}
}
