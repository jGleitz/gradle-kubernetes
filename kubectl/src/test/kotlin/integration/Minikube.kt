package de.joshuagleitze.gradle.kubectl.integration

import org.apache.commons.io.output.TeeOutputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
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
		ByteArrayOutputStream().use { errorBuffer ->
			startProcess.transferOutput(errorStream = TeeOutputStream(System.err, errorBuffer))
			check(startProcess.waitFor() == 0) {
				"Failed to create the minikube cluster! Error output was: ${System.lineSeparator()}${System.lineSeparator()}$errorBuffer"
			}
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
			check(commandProcess.runAndWaitFor() == 0) {
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

		private fun scheduleStart(): OutputBufferingProcess {
			// if the previous test was aborted, the minikube cluster might be in an inconsistent state. Hence, we run delete first. It is
			// quick and improves our chances of starting the cluster successfully. We do not care about the result of deleting the previous
			// cluster, but only about starting the new one.
			val deleteProcess = minikube("delete")
			val startProcess = OutputBufferingProcess(minikube("start", "--interactive=false"))
			Thread {
				deleteProcess.start().waitFor()
				startProcess.run()
			}.start()
			return startProcess
		}

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
			if (instanceCount.decrementAndGet() <= 0) stop()
		}

			private fun stop() {
				ByteArrayOutputStream().use { errorBuffer ->
					val stopProcess = OutputBufferingProcess(minikube("delete"))
						.transferOutput(errorStream = TeeOutputStream(System.err, errorBuffer))
					check(stopProcess.runAndWaitFor() == 0) {
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
				outputTarget.use { process.inputStream.backportedTransferTo(it) }
			}.startAndRegisterToWaitFor()
			Thread {
				errorTarget.use { process.errorStream.backportedTransferTo(it) }
			}.startAndRegisterToWaitFor()

			val exitValue = process.waitFor()
			synchronized(threadsToWaitFor) {
				threadsToWaitFor.forEach { it.join() }
			}
			exitValue
		}

		fun waitFor(): Int = task.get()

		fun runAndWaitFor(): Int {
			run()
			return waitFor()
		}

		fun transferOutput(outputStream: OutputStream = System.out, errorStream: OutputStream = System.err) = apply {
			synchronized(threadsToWaitFor) {
				Thread { outputBuffer.use { it.backportedTransferTo(outputStream) } }.startAndRegisterToWaitFor()
				Thread { errorBuffer.use { it.backportedTransferTo(errorStream) } }.startAndRegisterToWaitFor()
			}
		}

		private fun Thread.startAndRegisterToWaitFor() {
			threadsToWaitFor += this
			this.start()
		}

		override fun run() = task.run()

		val isDone get() = task.isDone

		// InputStream.transferTo is only available since JDK 9 :(
		private fun InputStream.backportedTransferTo(out: OutputStream, bufferSize: Int = 8192): Long {
			var transferred: Long = 0
			val buffer = ByteArray(bufferSize)
			var read: Int
			while (this.read(buffer, 0, bufferSize).also { read = it } >= 0) {
				out.write(buffer, 0, read)
				transferred += read.toLong()
			}
			return transferred
		}
	}
}
