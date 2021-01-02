package de.joshuagleitze.gradle.kubectl.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.extra

public object DeployTask {
	public const val NAME: String = "deploy"
	public const val DEPLOYMENT_TASK_GROUP: String = "deployment"

	public fun register(project: Project) {
		project.tasks.findByName(NAME) ?: project.task(NAME) {
			it.group = DEPLOYMENT_TASK_GROUP
			it.description = "Executes all deployments in this project"
		}
	}

	public val TaskContainer.deploy: TaskProvider<Task> get() = this.named(NAME)

	@Suppress("UNCHECKED_CAST")
	public var Project.deployingTasks: Set<TaskProvider<out Task>>
		get() = if (this.extra.has("deployingTasks")) this.extra["deployingTasks"] as Set<TaskProvider<out Task>> else emptySet()
		private set(value) {
			this.extra["deployingTasks"] = value
		}

	public fun Project.registerDeploymentTask(task: Task): Unit = registerDeploymentTask(task.project.tasks.named(task.name))

	public fun Project.registerDeploymentTask(task: TaskProvider<out Task>) {
		tasks.deploy.configure { it.dependsOn(task) }
		deployingTasks += task
	}
}
