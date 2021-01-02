package de.joshuagleitze.gradle.kubectl

import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic._logic
import ch.tutteli.atrium.logic.changeSubject
import de.joshuagleitze.gradle.kubectl.data.Arguments

fun Expect<Arguments>.asList() = _logic.changeSubject.unreported { it.toList() }
