package de.joshuagleitze.gradle.kubernetes

import de.joshuagleitze.stringnotation.BaseStringNotation

public object GradleInputNotation: BaseStringNotation(Regex("[\\- _/]")) {
	override fun printBeforeInnerPart(index: Int, part: String): String = " "
}
