package de.joshuagleitze.gradle.kubectl.integration

import org.apache.commons.io.output.TeeOutputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.LinkedList
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicInteger

class Minikube private constructor(
	private val startProcess: OutputBufferingProcess,
	private val kubectlDownloadProcess: OutputBufferingProcess
) {
	fun awaitStart() {
		startProcess.transferOutput()
		check(startProcess.waitFor() == 0) {
			"Failed to create the minikube cluster!"
		}
	}

	fun kubectl(vararg args: String): String {
		check(startProcess.isDone) { "The minikube cluster has not started yet!" }
		check(startProcess.waitFor() == 0) { "The minikube cluster was not started successfully!" }
		kubectlDownloadProcess.transferOutput()
		check(kubectlDownloadProcess.waitFor() == 0) { "Could not download the kubectl executable" }

		return ByteArrayOutputStream().use { outputBuffer ->
			val commandProcess = OutputBufferingProcess(minikube("kubectl", "--", *args))
				.transferOutput(outputStream = TeeOutputStream(System.out, outputBuffer))
			commandProcess.run()
			check(commandProcess.waitFor() == 0) {
				"kubectl ${args.joinToString(separator = " ")} failed!"
			}
			outputBuffer.toString()
		}
	}

	fun stop() = deregister()

	companion object {
		private const val MINIKUBE_EXECUTABLE_PROPERTY_NAME = "minikube-executable"
		val CONTEXT_NAME = Minikube::class.qualifiedName!!
		private val instanceCount = AtomicInteger(0)
		private val instance by lazy {
			val startProcess = scheduleStart()
			val downloadProcess = scheduleKubectlDownload(startProcess)
			Minikube(startProcess, downloadProcess)
		}
		private val minikubeExecutable by lazy {
			System.getProperty(MINIKUBE_EXECUTABLE_PROPERTY_NAME)
				?: error("No minikube executable was provided. Please set the system property $MINIKUBE_EXECUTABLE_PROPERTY_NAME!")
		}

		private fun minikube(vararg args: String) = ProcessBuilder(minikubeExecutable, "--profile=$CONTEXT_NAME", *args)

		private fun scheduleStart() = OutputBufferingProcess(minikube("start", "--interactive=false")).also { Thread(it).start() }

		private fun scheduleKubectlDownload(startProcess: OutputBufferingProcess) =
			OutputBufferingProcess(minikube("kubectl", "--", "version")).also { downloadProcess ->
				Thread {
					startProcess.waitFor()
					downloadProcess.run()
				}.start()
			}

		fun use(): Minikube {
			instanceCount.incrementAndGet()
			return instance
		}

		private fun deregister() {
			if (instanceCount.decrementAndGet() <= 0) {
				val stopProcess = minikube("delete").inheritIO().start()
				check(stopProcess.waitFor() == 0) {
					"Failed to stop the minikube cluster!"
				}
			}
		}
	}

	private class OutputBufferingProcess(processBuilder: ProcessBuilder): Runnable {
		private val outputBuffer = PipedInputStream(65536)
		private val errorBuffer = PipedInputStream(65536)
		private val outputTarget = PipedOutputStream(outputBuffer)
		private val errorTarget = PipedOutputStream(errorBuffer)
		private val threadsToWaitFor = LinkedList<Thread>()
		private val task = FutureTask {
			val process = processBuilder.start()
			Thread {
				outputTarget.use { outputPipe ->
					process.inputStream.transferTo(outputPipe)
				}
			}.startAndRegisterToWaitFor()
			Thread {
				errorTarget.use { errorPipe ->
					process.errorStream.transferTo(errorPipe)
				}
			}.startAndRegisterToWaitFor()

			val exitValue = process.waitFor()
			synchronized(threadsToWaitFor) {
				threadsToWaitFor.forEach { it.join() }
			}
			exitValue
		}

		fun waitFor(): Int = task.get()

		fun transferOutput(outputStream: OutputStream = System.out, errorStream: OutputStream = System.err) = apply {
			synchronized(threadsToWaitFor) {
				Thread { outputBuffer.transferTo(outputStream) }.startAndRegisterToWaitFor()
				Thread { errorBuffer.transferTo(errorStream) }.startAndRegisterToWaitFor()
			}
		}

		private fun Thread.startAndRegisterToWaitFor() {
			threadsToWaitFor += this
			this.start()
		}

		override fun run() = task.run()

		val isDone get() = task.isDone
	}
}
