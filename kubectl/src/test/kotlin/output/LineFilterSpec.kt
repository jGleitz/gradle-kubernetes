package de.joshuagleitze.gradle.kubectl.output

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.test.describeType
import io.mockk.spyk
import io.mockk.verify
import org.spekframework.spek2.Spek
import java.io.ByteArrayOutputStream
import java.nio.file.Path

object LineFilterSpec: Spek({
	describeType<LineFilter> {
		it("writes a ByteArray through to the target stream") {
			val targetStream = ByteArrayOutputStream()
			val lineFilter = LineFilter(targetStream) { true }
			lineFilter.write("test".toByteArray())
			lineFilter.flush()

			expect(targetStream.toString()).toBe("test")
		}

		it("writes a ByteArray with positions through to the target stream") {
			val targetStream = ByteArrayOutputStream()
			val lineFilter = LineFilter(targetStream) { true }
			lineFilter.write("a test x".toByteArray(), 2, 4)
			lineFilter.flush()

			expect(targetStream.toString()).toBe("test")

			expect(Path.of("/foo/bar")).contains(Path.of("bar"))
		}


		it("writes an int through to the target stream") {
			val targetStream = ByteArrayOutputStream()
			val lineFilter = LineFilter(targetStream) { true }
			lineFilter.write('t'.toInt())
			lineFilter.flush()

			expect(targetStream.toString()).toBe("t")
		}

		it("removes filtered lines") {
			val targetStream = ByteArrayOutputStream()
			val lineFilter = LineFilter(targetStream) { !it.contains("two") }
			lineFilter.write(
				"""
				one
				two
				three
			""".trimIndent().toByteArray()
			)
			lineFilter.flush()

			expect(targetStream.toString()).toBe(
				"""
				one
				three
			""".trimIndent()
			)
		}

		it("counts removed lines") {
			val targetStream = ByteArrayOutputStream()
			val lineFilter = LineFilter(targetStream) { !it.contains("out") }
			lineFilter.write(
				"""
				in
				out
				out
				in
				in
				out
				out
				out
				in
				out
				in
			""".trimIndent().toByteArray()
			)
			lineFilter.flush()

			expect(lineFilter).feature(LineFilter::removedLinesCount).toBe(6)
		}

		it("flushes the underlying stream") {
			val targetStream = spyk(ByteArrayOutputStream())
			val lineFilter = LineFilter(targetStream) { true }

			lineFilter.flush()
			verify {
				targetStream.flush()
			}
		}

		it("closes the underlying stream") {
			val targetStream = spyk(ByteArrayOutputStream())
			val lineFilter = LineFilter(targetStream) { true }

			lineFilter.close()
			verify {
				targetStream.close()
			}
		}
	}
})
