package de.joshuagleitze.gradle

import de.joshuagleitze.stringnotation.BaseStringNotation

public object GradleInputNotation: BaseStringNotation(Regex("[\\- _/]")) {
	override fun printBeforeInnerPart(index: Int, part: String) = " "
}
