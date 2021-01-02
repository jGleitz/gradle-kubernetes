package de.joshuagleitze.test

import java.nio.file.Files.walk
import java.nio.file.Files.delete
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.Comparator.reverseOrder

inline fun <reified T: Throwable> tolerate(block: () -> Unit) {
	val exception = runCatching(block).exceptionOrNull()
	if (exception !== null && exception !is T) throw exception
}

fun Path.clearIfExists() {
	tolerate<NoSuchFileException> {
		walk(this).sorted(reverseOrder()).forEach(::delete)
	}
}
