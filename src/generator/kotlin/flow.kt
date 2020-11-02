package de.joshuagleitze.gradle.kubectl.generator

import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.produceIn
import java.io.Closeable

@Suppress("BlockingMethodInNonBlockingContext")
inline fun <C: Closeable, T, F: Flow<T>> flowUsing(closeable: C, block: (C) -> F) = block(closeable).onCompletion { closeable.close() }

inline fun <C: Closeable, T1, T2> Flow<T1>.using(closeable: C, block: Flow<T1>.(C) -> Flow<T2>) = flowUsing(closeable) { block(it) }

// from https://github.com/Kotlin/kotlinx.coroutines/issues/2193#issuecomment-705655058
fun <T: Any> Flow<T>.bufferedChunks(maxChunkSize: Int): Flow<List<T>> {
	require(maxChunkSize >= 1)
	return flow<List<T>> {
		coroutineScope {
			val upstreamChannel = this@bufferedChunks.buffer(maxChunkSize).produceIn(this)
			while (true) { // loop until closed
				val bufferChunks = ArrayList<T>(maxChunkSize) // allocate new array list every time
				// receive the first element (suspend until it is there)
				// null here means the channel was closed -> terminate the outer loop
				val first = upstreamChannel.receiveOrNull() ?: break
				bufferChunks.add(first)
				while (bufferChunks.size < maxChunkSize) {
					// poll subsequent elements from the channel's buffer without waiting while they are present
					// null here means there are no more element or channel was closed -> break from this loop
					val element = upstreamChannel.poll() ?: break
					bufferChunks.add(element)
				}
				emit(bufferChunks)
			}
		}
	}
}
