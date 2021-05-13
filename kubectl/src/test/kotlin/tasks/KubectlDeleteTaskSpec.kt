package de.joshuagleitze.gradle.kubectl.tasks

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.KubectlPlugin
import de.joshuagleitze.gradle.kubectl.action.KubectlAction
import de.joshuagleitze.gradle.kubectl.action.mockWorkerExecutorFor
import de.joshuagleitze.gradle.kubectl.data.LabelSelector
import de.joshuagleitze.gradle.kubectl.data.Selector
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask.Companion.downloadKubectl
import de.joshuagleitze.gradle.kubectl.tasks.TeardownTask.TEARDOWN_TASK_GROUP
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigContext
import de.joshuagleitze.test.describeType
import de.joshuagleitze.test.get
import de.joshuagleitze.testfiles.kotest.testFiles
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.DescribeSpec
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.util.*

class KubectlDeleteTaskSpec : DescribeSpec({
	isolationMode = InstancePerTest

	val createdParameters = LinkedList<KubectlAction.Parameters>()
	lateinit var testProject: Project
	lateinit var mockWorkerExecutor: WorkerExecutor

	beforeEach {
		testProject = ProjectBuilder.builder()
			.withProjectDir(testFiles.createDirectory("testProject").toFile())
			.build()
			.also { it.plugins.apply(KubectlPlugin::class) }
		mockWorkerExecutor = testProject.mockWorkerExecutorFor(KubectlAction::class, createdParameters)
	}

	fun registerKubectlDeleteTask(
		name: String,
		project: Project = testProject,
		workerExecutor: WorkerExecutor = mockWorkerExecutor,
		configuration: KubectlDeleteTask.() -> Unit = {}
	): KubectlDeleteTask {
		project.tasks.downloadKubectl.configure {
			if (!it.outputs.hasOutput) it.outputs.file(project.projectDir.resolve("testExecutable"))
		}
		return project.tasks.create(name, KubectlDeleteTask::class.java, workerExecutor).apply {
			kustomizationDir.set(project.projectDir.resolve("kustomization"))
			cluster.set(KubeconfigContext("test"))
			configuration()
		}
	}

	describeType<KubectlDeleteTask> {
		it("sets the action to 'delete'") {
			registerKubectlDeleteTask("deleteAction").delete()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.get(0).toBe("delete")
		}

		it("rejects to run if no kustomizationDir and no selector was set") {
			val deleteTask = registerKubectlDeleteTask("cannotRun") {
				kustomizationDir.set(null as File?)
				selector.set(null as Selector?)
			}

			expect {
				deleteTask.delete()
			}.toThrow<IllegalArgumentException>().messageContains("kustomizationDir", "selector")
		}

		it("sets the --kustomize option") {
			val testKustomizationDir = testFiles.createDirectory("kustomization")
			registerKubectlDeleteTask("kustomizationOption") {
				kustomizationDir.set(testKustomizationDir.toFile())
			}.delete()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--kustomize=$testKustomizationDir")
		}

		it("appends the options of the selector") {
			registerKubectlDeleteTask("selectorOptions") {
				selector.set(LabelSelector("labelA" to "one", "fruit" to "vegetable"))
			}.delete()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--selector=labelA=one,fruit=vegetable")
		}

		it("defaults ${KubectlDeleteTask::waitForResourceDeletion.name} to false") {
			val applyTask = registerKubectlDeleteTask("waitIsFalse")

			expect(applyTask)
				.feature(KubectlDeleteTask::waitForResourceDeletion)
				.toBe(false)
		}

		it("appends the --wait option if requested") {
			registerKubectlDeleteTask("generatesWait") {
				waitForResourceDeletion = true
			}.delete()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--wait")
		}

		it("does not append the --wait option if not requested") {
			registerKubectlDeleteTask("generatesNoWait") {
				waitForResourceDeletion = false
			}.delete()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.containsNot("--wait")
		}

		it("puts itself into the $TEARDOWN_TASK_GROUP group") {
			val deleteTask = registerKubectlDeleteTask("correctGroup")

			expect(deleteTask)
				.feature(Task::getGroup)
				.toBe(TEARDOWN_TASK_GROUP)
		}

		it("has a task description") {
			val deleteTask = registerKubectlDeleteTask("hasDescription")

			expect(deleteTask)
				.feature(Task::getDescription)
				.notToBeNull().isNotEmpty()
		}
	}

	include(
		kubectlTaskSpec(
			testProject = { testProject },
			createTask = { name, workerExecutor -> registerKubectlDeleteTask(name, this, workerExecutor) },
			execute = { delete() }
		)
	)
})
