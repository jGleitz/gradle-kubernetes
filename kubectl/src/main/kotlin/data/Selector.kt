package de.joshuagleitze.gradle.kubectl.data

public interface Selector {
	public fun generateKubectlArguments(): Arguments
}

public class LabelSelector(public val labels: Map<String, String>): Selector {
	init {
		require(labels.isNotEmpty()) {
			"A label selector must define at least one label"
		}
		labels.forEach { (key, value) ->
			require(key.isNotEmpty()) {
				"A label key must not be empty, but found '=$value'"
			}
		}
	}

	public constructor(vararg labels: Pair<String, String>): this(mapOf(*labels))

	override fun generateKubectlArguments(): Arguments =
		Arguments(labels.entries.joinToString(prefix = "--selector=", separator = ",") { (name, value) -> "$name=$value" })
}
