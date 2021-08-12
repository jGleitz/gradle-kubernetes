package de.joshuagleitze.test

import ch.tutteli.atrium.api.fluent.en_GB.asPath
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.withRepresentation
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic._logic
import ch.tutteli.atrium.logic.changeSubject
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.internal.instantiation.InstantiatorFactory
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.service.ServiceLookup
import org.gradle.internal.service.UnknownServiceException
import org.gradle.kotlin.dsl.support.serviceOf
import java.lang.reflect.Type

fun <T, P: Provider<out T>> Expect<P>.get() = feature(Provider<out T>::get)

fun <T: Property<out FileSystemLocation>> Expect<T>.getAsFile() = get()._logic.changeSubject.unreported { it.asFile }

fun <T: Property<out FileSystemLocation>> Expect<T>.getAsPath() = getAsFile().asPath()

val <T: Project> Expect<T>.tasks get() = feature(Project::getTasks).withRepresentation { it.names }

fun <T: Project> Expect<T>.tasks(block: Expect<TaskContainer>.() -> Unit) = tasks.addAssertionsCreatedBy(block)

val <T: Task> Expect<T>.name get() = feature(Task::getName)

fun <T: Any, C: NamedDomainObjectCollection<T>> Expect<C>.findByName(name: String) =
	feature(NamedDomainObjectCollection<T>::findByName, name)

fun <T: Any, C: NamedDomainObjectCollection<T>> Expect<C>.findByName(name: String, block: Expect<T?>.() -> Unit) =
	findByName(name).addAssertionsCreatedBy(block)

inline fun <reified T: Any, C: NamedDomainObjectCollection<T>> Expect<C>.getByName(name: String) = findByName(name).notToBeNull()
inline fun <reified T: Any, C: NamedDomainObjectCollection<T>> Expect<C>.getByName(name: String, noinline block: Expect<T>.() -> Unit) =
	getByName(name).addAssertionsCreatedBy(block)

val <T: Task> Expect<T>.dependencies
	get() = feature("dependencies") {
		taskDependencies.getDependencies(this)
	}

val <T: Task> Expect<T>.mustRunAfter
	get() = feature("mustRunAfter") {
		this.mustRunAfter.getDependencies(this)
	}

val Project.instantiator: Instantiator
	get() {
		this as ProjectInternal
		return serviceOf<InstantiatorFactory>()
			.decorateScheme().withServices(services + objects)
			.instantiator()
	}

operator fun ServiceLookup.plus(service: Any) = object: ServiceLookup {
	override fun find(serviceType: Type) = this@plus.find(serviceType) ?: ourService(serviceType)

	override fun get(serviceType: Type) =
		try {
			this@plus.get(serviceType)
		} catch (e: UnknownServiceException) {
			ourService(serviceType) ?: throw e
		}

	override fun get(serviceType: Type, annotatedWith: Class<out Annotation>?) =
		try {
			this@plus.get(serviceType, annotatedWith)
		} catch (e: UnknownServiceException) {
			ourService(serviceType) ?: throw e
		}

	private fun ourService(serviceType: Type) =
		if (service::class.java.isAssignableFrom(serviceType.javaClass)) service
		else null
}
