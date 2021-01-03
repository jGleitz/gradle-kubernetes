package de.joshuagleitze.gradle.kubectl.tasks

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.containsNot
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.get
import ch.tutteli.atrium.api.fluent.en_GB.isNotEmpty
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.KubectlPlugin
import de.joshuagleitze.gradle.kubectl.action.KubectlAction
import de.joshuagleitze.gradle.kubectl.action.mockWorkerExecutorFor
import de.joshuagleitze.gradle.kubectl.data.LabelSelector
import de.joshuagleitze.gradle.kubectl.tasks.DeployTask.DEPLOYMENT_TASK_GROUP
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigContext
import de.joshuagleitze.test.describeType
import de.joshuagleitze.test.get
import de.joshuagleitze.test.spek.testfiles.testFiles
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.workers.WorkerExecutor
import org.spekframework.spek2.Spek
import java.util.LinkedList

object KubectlApplyTaskSpec: Spek({
	val testFiles = testFiles()
	val testProject by memoized {
		ProjectBuilder.builder()
			.withProjectDir(testFiles.createDirectory("testProject").toFile())
			.build()
			.also { it.plugins.apply(KubectlPlugin::class) }
	}
	val createdParameters by memoized { LinkedList<KubectlAction.Parameters>() }
	val mockWorkerExecutor by memoized { testProject.mockWorkerExecutorFor(KubectlAction::class, createdParameters) }

	fun registerKubectlApplyTask(
		name: String,
		project: Project = testProject,
		workerExecutor: WorkerExecutor = mockWorkerExecutor,
		configuration: KubectlApplyTask.() -> Unit = {}
	): KubectlApplyTask {
		return project.tasks.create(name, KubectlApplyTask::class.java, workerExecutor).apply {
			kustomizationDir.set(project.projectDir.resolve("kustomization"))
			cluster.set(KubeconfigContext("test"))
			configuration()
		}
	}

	describeType<KubectlApplyTask> {
		it("sets the action to 'apply'") {
			registerKubectlApplyTask("applyAction").apply()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.get(0).toBe("apply")
		}

		it("sets the --kustomize option") {
            val testKustomizationDir = testFiles.createDirectory("kustomization")
			registerKubectlApplyTask("kustomizationOption") {
				kustomizationDir.set(testKustomizationDir.toFile())
			}.apply()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--kustomize=$testKustomizationDir")
		}

		it("puts itself into the $DEPLOYMENT_TASK_GROUP group") {
			val applyTask = registerKubectlApplyTask("correctGroup")

			expect(applyTask)
				.feature(Task::getGroup)
				.toBe(DEPLOYMENT_TASK_GROUP)
		}

		it("has a task description") {
			val applyTask = registerKubectlApplyTask("hasDescription")

			expect(applyTask)
				.feature(Task::getDescription)
				.notToBeNull().isNotEmpty()
		}

		it("defaults ${KubectlApplyTask::waitForResourceDeletion.name} to false") {
			val applyTask = registerKubectlApplyTask("waitIsFalse")

			expect(applyTask)
				.feature(KubectlApplyTask::waitForResourceDeletion)
				.toBe(false)
		}

		it("appends the --wait option if requested") {
			registerKubectlApplyTask("generatesWait") {
				waitForResourceDeletion = true
			}.apply()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--wait")
		}

		it("does not append the --wait option if not requested") {
			registerKubectlApplyTask("generatesNoWait") {
				waitForResourceDeletion = false
			}.apply()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.containsNot("--wait")
		}

		it("appends the options for a prune selector") {
			registerKubectlApplyTask("pruneOptions") {
				pruneSelector.set(LabelSelector("labelA" to "one", "fruit" to "vegetable"))
			}.apply()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--prune", "--selector=labelA=one,fruit=vegetable")
				.containsNot("testLabel")
		}
	}

	include(
		object: KubectlTaskSpec<KubectlApplyTask>(
			testProject = { testProject },
			createTask = { name, workerExecutor -> registerKubectlApplyTask(name, this, workerExecutor) },
			execute = { apply() }
		) {}
	)
})
