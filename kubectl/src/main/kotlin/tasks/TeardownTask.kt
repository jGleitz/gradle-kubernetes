package de.joshuagleitze.gradle.kubectl.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.extra

public object TeardownTask {
	public const val NAME: String = "teardown"
	public const val TEARDOWN_TASK_GROUP: String = "teardown"

	public fun register(project: Project) {
		project.tasks.findByName(NAME) ?: project.task(NAME) {
			it.group = TEARDOWN_TASK_GROUP
			it.description = "Executes all teardowns in this project"
		}
	}

	public val TaskContainer.teardown: TaskProvider<Task> get() = this.named(NAME)

	@Suppress("UNCHECKED_CAST")
	public var Project.teardownTasks: Set<TaskProvider<out Task>>
		get() = if (this.extra.has("teardownTasks")) this.extra["teardownTasks"] as Set<TaskProvider<out Task>> else emptySet()
		private set(value) {
			this.extra["teardownTasks"] = value
		}

	public fun Project.registerTeardownTask(task: Task): Unit = registerTeardownTask(task.project.tasks.named(task.name))

	public fun Project.registerTeardownTask(task: TaskProvider<out Task>) {
		tasks.teardown.configure { it.dependsOn(task) }
		teardownTasks += task
	}
}
