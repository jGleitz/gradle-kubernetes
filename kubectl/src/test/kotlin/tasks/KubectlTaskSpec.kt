package de.joshuagleitze.gradle.kubectl.tasks

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isSameAs
import ch.tutteli.atrium.api.fluent.en_GB.messageContains
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.action.KubectlAction
import de.joshuagleitze.gradle.kubectl.action.mockWorkerExecutorFor
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask.Companion.downloadKubectl
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableVerificationTask.Companion.verifyKubectl
import de.joshuagleitze.gradle.kubernetes.data.KubeconfigContext
import de.joshuagleitze.gradle.kubernetes.data.KubernetesClusterConnection
import de.joshuagleitze.test.dependencies
import de.joshuagleitze.test.get
import de.joshuagleitze.test.getAsFile
import de.joshuagleitze.test.spek.testfiles.testFiles
import io.mockk.confirmVerified
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.util.LinkedList

abstract class KubectlTaskSpec<KubectlTaskType: KubectlTask>(
	testProject: () -> Project,
	createTask: Project.(name: String, workerExecutor: WorkerExecutor) -> KubectlTaskType,
	execute: KubectlTaskType.() -> Unit
): Spek({
    val testFiles = testFiles()
	val createdParameters by memoized { LinkedList<KubectlAction.Parameters>() }
	val mockWorkerExecutor by memoized { testProject().mockWorkerExecutorFor(KubectlAction::class, createdParameters) }
	fun newTask(name: String) = testProject().createTask(name, mockWorkerExecutor)

	describe("${KubectlTask::class.simpleName} functionality") {
		it("rejects being executed without a configured cluster") {
			val testTask = newTask("withoutCluster")
			testTask.cluster.set(null as KubernetesClusterConnection?)

			expect { testTask.execute() }
				.toThrow<IllegalArgumentException>().messageContains("cluster")
			confirmVerified(mockWorkerExecutor)
		}

		it("adds the options for the cluster") {
			val testTask = newTask("dependsOnVerify")
			testTask.cluster.set(KubeconfigContext("testcontext"))
			testTask.execute()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::arguments).get()
				.contains("--context=testcontext")
		}

		it("sets the executable to the output of the download task") {
			val testExecutable = testFiles.createFile("kubectl").toFile()
			val testTask = newTask("setsExecutable")
			testTask.project.tasks.downloadKubectl.configure {
				it.targetFile.set(testExecutable)
			}

			testTask.execute()

			expect(createdParameters.single())
				.feature(KubectlAction.Parameters::executable).getAsFile()
				.toBe(testExecutable)
		}

		it("depends on ${KubectlExecutableDownloadTask.NAME}") {
			val testTask = newTask("dependsOnDownload")
			expect(testTask)
				.dependencies
				.contains(testTask.project.tasks.downloadKubectl.get())
		}

		it("depends on ${KubectlExecutableVerificationTask.NAME}") {
			val testTask = newTask("dependsOnVerify")
			expect(testTask)
				.dependencies
				.contains(testTask.project.tasks.verifyKubectl.get())
		}

		it("defaults the log file path according to the task name") {
			val testTask = newTask("withTestLogFile")

			expect(testTask)
				.feature(KubectlTask::logFile)
				.getAsFile()
				.toBe(testTask.project.buildDir.resolve("reports/kubectl/withTestLogFile.log"))
		}

		it("defaults groupUnchangedMessages to true") {
			val testTask = newTask("groupUnchangedMessagesDefault")

			expect(testTask)
				.feature(KubectlTask::groupUnchangedMessages)
				.toBe(true)
		}
	}
})
