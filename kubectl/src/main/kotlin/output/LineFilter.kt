package de.joshuagleitze.gradle.kubectl.output

import org.apache.commons.io.output.WriterOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.atomic.AtomicInteger

internal class LineFilter(out: OutputStream, charset: Charset = UTF_8, predicate: (String) -> Boolean): OutputStream() {
	private val writer = LineFilterWriter(out, charset, predicate)
	private val writerOutputStream = WriterOutputStream(writer, charset)
	val removedLinesCount get() = writer.removedLinesCount.get()

	private class LineFilterWriter(out: OutputStream, charset: Charset = UTF_8, val filter: (String) -> Boolean): Writer() {
		private var buffer = StringBuffer()
		private val charOut = OutputStreamWriter(out, charset)
		val removedLinesCount = AtomicInteger(0)

		override fun write(cbuf: CharArray, off: Int, len: Int) {
			writeCharArrayLike(cbuf, off, len, CharArray::get, StringBuffer::append)
		}

		override fun write(str: String, off: Int, len: Int) {
			writeCharArrayLike(str, off, len, String::get, StringBuffer::append)
		}

		override fun write(c: Int) {
			buffer.append(c)
			if (c == '\n'.toInt()) {
				filterAndWrite()
			}
		}

		private inline fun <T> writeCharArrayLike(
			charArrayLike: T,
			offset: Int,
			length: Int,
			charAt: T.(Int) -> Char,
			append: StringBuffer.(T, Int, Int) -> StringBuffer
		) {
			var newLineIndex: Int = offset - 1
			var firstPartLength: Int
			for (index in offset until (offset + length)) {
				if (charArrayLike.charAt(index) == '\n') {
					val oldIndex = newLineIndex
					newLineIndex = index
					firstPartLength = newLineIndex - oldIndex
					buffer.append(charArrayLike, oldIndex + 1, firstPartLength)
					filterAndWrite()
				}
			}
			buffer.append(charArrayLike, newLineIndex + 1, offset + length - newLineIndex - 1)
		}

		private fun filterAndWrite() {
			val bufferContent = buffer.toString()
			buffer = StringBuffer()
			if (filter(bufferContent)) {
				charOut.write(bufferContent)
			} else {
				removedLinesCount.incrementAndGet()
			}
		}

		override fun flush() {
			filterAndWrite()
			charOut.flush()
		}

		override fun close() {
			charOut.close()
		}
	}

	override fun write(b: Int) {
		writerOutputStream.write(b)
	}

	override fun write(b: ByteArray) {
		writerOutputStream.write(b)
	}

	override fun write(b: ByteArray, off: Int, len: Int) {
		writerOutputStream.write(b, off, len)
	}

	override fun flush() {
		writerOutputStream.flush()
	}

	override fun close() {
		writerOutputStream.close() // will also close writer
	}
}
