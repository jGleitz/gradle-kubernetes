package de.joshuagleitze.test

import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.Description
import org.spekframework.spek2.meta.DescriptionLocation.TYPE_PARAMETER
import org.spekframework.spek2.meta.Descriptions
import org.spekframework.spek2.meta.Synonym
import org.spekframework.spek2.meta.SynonymType.GROUP
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.time.seconds

@Synonym(GROUP)
@Descriptions(Description(TYPE_PARAMETER, 0))
inline fun <reified T> GroupBody.describeType(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) =
	describe(typeDescription(T::class.java), skip, body)

fun typeDescription(klass: Class<*>): String =
	when (val declaringClass = klass.declaringClass) {
		null -> klass.simpleName
		else -> typeDescription(declaringClass) + "." + klass.simpleName
	}

fun forGradleTest(): Long = 180.seconds.toLongMilliseconds()

fun forMinikubeTest(): Long = 120.seconds.toLongMilliseconds()
