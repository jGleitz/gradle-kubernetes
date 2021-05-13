package de.joshuagleitze.gradle.kubectl.tasks

import ch.tutteli.atrium.api.fluent.en_GB.*
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
import de.joshuagleitze.testfiles.kotest.testFiles
import io.kotest.core.spec.style.describeSpec
import io.mockk.confirmVerified
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor
import java.util.*

fun <KubectlTaskType : KubectlTask> kubectlTaskSpec(
	testProject: () -> Project,
	createTask: Project.(name: String, workerExecutor: WorkerExecutor) -> KubectlTaskType,
	execute: KubectlTaskType.() -> Unit
) = describeSpec {
	val createdParameters = LinkedList<KubectlAction.Parameters>()
	lateinit var mockWorkerExecutor: WorkerExecutor
	fun newTask(name: String) = testProject().createTask(name, mockWorkerExecutor)

	beforeEach {
		mockWorkerExecutor = testProject().mockWorkerExecutorFor(KubectlAction::class, createdParameters)
	}

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
}
