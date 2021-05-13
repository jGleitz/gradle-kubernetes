package de.joshuagleitze.gradle.kubectl.output

import ch.tutteli.atrium.api.fluent.en_GB.isSameAs
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.output.DelegatingOutputStream.Companion.blockClose
import de.joshuagleitze.gradle.kubectl.output.DelegatingOutputStream.Companion.onClose
import de.joshuagleitze.test.describeType
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.confirmVerified
import io.mockk.excludeRecords
import io.mockk.spyk
import io.mockk.verifyAll
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
class DelegatingOutputStreamSpec : DescribeSpec({
	isolationMode = InstancePerTest

	val targetOutputStream = spyk(ByteArrayOutputStream())
	val delegatingOutputStream = DelegatingOutputStream(targetOutputStream)

	describeType<DelegatingOutputStream> {
		it("delegates write(Int)") {
			delegatingOutputStream.write(42)
			verifyAll {
				targetOutputStream.write(42)
			}
		}

		it("delegates write(ByteArray)") {
			excludeRecords {
				targetOutputStream.write(any(), any(), any())
			}

			val data = byteArrayOf(13, 42, 6, 127, 50)
			delegatingOutputStream.write(data)
			verifyAll {
				targetOutputStream.write(data)
			}
		}

		it("delegates write(ByteArray, Int, Int)") {
			val data = byteArrayOf(13, 42, 6, 127, 50)
			delegatingOutputStream.write(data, 1, 3)
			verifyAll {
				targetOutputStream.write(data, 1, 3)
			}
		}

		it("delegates flush()") {
			delegatingOutputStream.flush()
			verifyAll {
				targetOutputStream.flush()
			}
		}

		it("delegates close()") {
			delegatingOutputStream.close()
			verifyAll {
				targetOutputStream.close()
			}
		}

		it("offers an onClose hook") {
			val onCloseHook = { stream: OutputStream -> expect(stream).isSameAs(targetOutputStream); Unit }
			targetOutputStream.onClose(onCloseHook).close()
			verifyAll {
				targetOutputStream.close()
				onCloseHook(targetOutputStream)
			}
		}

		it("offers to block the close call") {
			val testOutputStream = spyk(ByteArrayOutputStream())
			testOutputStream.blockClose().close()

			confirmVerified(testOutputStream)
		}
	}
})
