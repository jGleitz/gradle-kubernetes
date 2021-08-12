package de.joshuagleitze.test

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerContext
import kotlin.time.seconds

inline fun <reified T> DescribeSpec.describeType(noinline test: suspend DescribeSpecContainerContext.() -> Unit) =
        describe(typeDescription(T::class.java), test)

fun typeDescription(klass: Class<*>): String =
        when (val declaringClass = klass.declaringClass) {
            null -> klass.simpleName
            else -> typeDescription(declaringClass) + "." + klass.simpleName
        }

fun forGradleTest(): Long = 180.seconds.toLongMilliseconds()

fun forMinikubeTest(): Long = 120.seconds.toLongMilliseconds()
