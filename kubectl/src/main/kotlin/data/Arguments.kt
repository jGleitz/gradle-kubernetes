package de.joshuagleitze.gradle.kubectl.data

import org.gradle.api.provider.Property

@Suppress("NOTHING_TO_INLINE")
public inline class Arguments(public val content: Sequence<String>) {
	public constructor(vararg arguments: String): this(arguments.asSequence())

	public inline fun <P: Any> addAllIfNotNull(value: P?, block: (P) -> Arguments): Arguments =
		if (value != null) this + block(value) else this

	public inline fun <P: Any> addIfNotNull(value: P?, block: (P) -> String): Arguments =
		if (value != null) this + block(value) else this

	public inline fun addAllIf(condition: Boolean, block: () -> Arguments): Arguments = if (condition) this + block() else this

	public inline fun addIf(condition: Boolean, block: () -> String): Arguments = if (condition) this + block() else this

	public inline fun <P> addAllIfPresent(property: Property<P>, block: (P) -> Arguments): Arguments =
		property.orNull?.let { this + block(it) } ?: this

	public inline fun <P> addIfPresent(property: Property<P>, block: (P) -> String): Arguments =
		property.orNull?.let { this + block(it) } ?: this

	public inline operator fun plus(arguments: Arguments): Arguments = Arguments(this.content + arguments.content)
	public inline fun addAll(arguments: Arguments): Arguments = this + arguments

	public inline operator fun plus(argument: String): Arguments = Arguments(this.content + argument)
	public inline fun add(argument: String): Arguments = this + argument

	public inline fun toList(): List<String> = content.toList()

	public companion object {
		public val None: Arguments = Arguments(emptySequence())
		public fun noArguments(): Arguments = None
	}
}
