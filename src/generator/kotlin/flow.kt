package de.joshuagleitze.gradle.kubectl.generator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import java.io.Closeable

inline fun <C: Closeable, T, F: Flow<T>> C.useForFlow(block: (C) -> F) = block(this).onCompletion { close() }
