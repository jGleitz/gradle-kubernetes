package de.joshuagleitze.gradle.kubectl.output

import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean

internal open class DelegatingOutputStream(val delegate: OutputStream): OutputStream() {
	override fun close(): Unit = delegate.close()

	override fun flush() = delegate.flush()

	override fun write(b: Int) = delegate.write(b)

	override fun write(b: ByteArray) = delegate.write(b)

	override fun write(b: ByteArray, off: Int, len: Int) = delegate.write(b, off, len)

	companion object {
		inline fun <T: OutputStream> T.onClose(crossinline block: (T) -> Unit) =
			object: DelegatingOutputStream(this@onClose) {
				override fun close() {
					super.close()
					block(this@onClose)
				}
			}

		fun <T: OutputStream> T.blockClose() =
			object: DelegatingOutputStream(this@blockClose) {
				override fun close() {
					/* blocked */
				}
			}

		fun <T: OutputStream> T.closeOnce() =
			object: DelegatingOutputStream(this@closeOnce) {
				private val closed = AtomicBoolean(false)
				override fun close() {
					if (closed.compareAndSet(false, true)) {
						super.close()
					}
				}
			}
	}
}
