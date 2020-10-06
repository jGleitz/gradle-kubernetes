package de.joshuagleitze.gradle.kubectl.data

data class Version(val major: Int, val minor: Int, val patch: Int): Comparable<Version> {
	fun toScreamingSnakeCaseNotation() = "V${major}_${minor}_$patch"
	fun toLowercaseNotation() = "v$major.$minor.$patch"
	override fun toString() = toLowercaseNotation()

	companion object {
		val LOWERCASE_NOTATION_PATTERN = Regex("^v(\\d+)\\.(\\d+)\\.(\\d+)$")
		fun parse(versionString: String) =
			LOWERCASE_NOTATION_PATTERN.matchEntire(versionString)?.let { matchResult ->
				Version(matchResult.groups[1]!!.value.toInt(), matchResult.groups[2]!!.value.toInt(), matchResult.groups[3]!!.value.toInt())
			}
	}

	override fun compareTo(other: Version) = when {
		this.major != other.major -> this.major - other.major
		this.minor != other.minor -> this.minor - other.minor
		else -> this.patch - other.patch
	}
}
