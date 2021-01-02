package de.joshuagleitze.gradle.kubectl.data

import java.io.Serializable

public data class Version(val major: Int, val minor: Int, val patch: Int): Comparable<Version>, Serializable {
	public fun toScreamingSnakeCaseNotation(): String = "V${major}_${minor}_$patch"
	public fun toLowercaseNotation(): String = "v$major.$minor.$patch"
	override fun toString(): String = toLowercaseNotation()

	public companion object {
		private val LOWERCASE_NOTATION_PATTERN = Regex("^v(\\d+)\\.(\\d+)\\.(\\d+)$")
		public fun parse(versionString: String): Version? =
			LOWERCASE_NOTATION_PATTERN.matchEntire(versionString)?.let { matchResult ->
				Version(matchResult.groups[1]!!.value.toInt(), matchResult.groups[2]!!.value.toInt(), matchResult.groups[3]!!.value.toInt())
			}
	}

	override fun compareTo(other: Version): Int = when {
		this.major != other.major -> this.major - other.major
		this.minor != other.minor -> this.minor - other.minor
		else -> this.patch - other.patch
	}
}
