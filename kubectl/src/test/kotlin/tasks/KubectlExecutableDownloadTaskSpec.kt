package de.joshuagleitze.gradle.kubectl.tasks

import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.extension
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.messageContains
import ch.tutteli.atrium.api.fluent.en_GB.notToThrow
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.KubectlVersion.V1
import de.joshuagleitze.gradle.kubectl.KubectlVersion.V1_17_6
import de.joshuagleitze.gradle.kubectl.KubectlVersion.V1_18_8
import de.joshuagleitze.gradle.kubectl.tasks.KubectlExecutableDownloadTask.Companion.createDownloadAction
import de.joshuagleitze.test.describeType
import de.joshuagleitze.test.getAsPath
import de.undercouch.gradle.tasks.download.DownloadAction
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.Architectures.ARM_V7
import org.gradle.nativeplatform.platform.internal.Architectures.IA_64
import org.gradle.nativeplatform.platform.internal.Architectures.X86
import org.gradle.nativeplatform.platform.internal.Architectures.X86_64
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.gradle.testfixtures.ProjectBuilder
import org.spekframework.spek2.Spek
import java.io.File
import java.net.URL
import kotlin.io.path.Path

object KubectlExecutableDownloadTaskSpec: Spek({
	describeType<KubectlExecutableDownloadTaskSpec> {
		val testProject by memoized { ProjectBuilder.builder().build() }
		val downloadAction by memoized {
			spyk(DownloadAction(testProject)) {
				every { execute() } returns Unit
				every { isUpToDate } returns false
			}
		}

		beforeGroup {
			mockkStatic(DefaultNativePlatform::class)
			mockkObject(KubectlExecutableDownloadTask)
		}
		beforeEachTest {
			every { any<Task>().createDownloadAction() } returns downloadAction
		}
		afterGroup { unmockkAll() }

		fun createKubectlDownloadTestTask() =
			testProject.tasks.create("testDownload", KubectlExecutableDownloadTask::class) {
				it.kubectlRelease.set(V1)
			}

		fun mockPlatform(osName: String, architecture: Architectures.KnownArchitecture) {
			every { DefaultNativePlatform.host() }
				.returns(DefaultNativePlatform("test", DefaultOperatingSystem(osName), Architectures.of(architecture)))
		}

		describe("architecture check") {
			listOf(X86, IA_64, ARM_V7).forEach { architecture ->
				it("rejects to run on ${architecture.canonicalName}") {
					mockPlatform("linux", architecture)
					val testTask = createKubectlDownloadTestTask()

					expect { testTask.download() }
						.toThrow<IllegalStateException>()
						.messageContains(architecture.canonicalName)
				}
			}

			it("runs on amd64") {
				mockPlatform("linux", X86_64)
				val testTask = createKubectlDownloadTestTask()

				expect { testTask.download() }
					.notToThrow()
			}
		}

		describe("operating system check") {
			listOf("sunos", "solaris", "freebsd").forEach { os ->
				it("rejects to run on $os") {
					mockPlatform(os, X86_64)
					val testTask = createKubectlDownloadTestTask()

					expect { testTask.download() }
						.toThrow<IllegalStateException>()
						.messageContains(os)
				}
			}
		}

		listOf("windows", "linux", "mac os x", "darwin", "osx").forEach { os ->
			it("runs on $os") {
				mockPlatform(os, X86_64)
				val testTask = createKubectlDownloadTestTask()

				expect { testTask.download() }
					.notToThrow()
			}
		}

		listOf(
			"windows" to "windows",
			"linux" to "linux",
			"mac os x" to "darwin",
			"darwin" to "darwin",
			"osx" to "darwin"
		).forEach { (gradleOsName, kubectlOsName) ->
			it("uses the $kubectlOsName URL for '$gradleOsName'") {
				mockPlatform(gradleOsName, X86_64)
				val testTask = createKubectlDownloadTestTask()

				testTask.download()
				expect(downloadAction.src)
					.isA<URL>()
					.feature(URL::toString)
					.contains("/$kubectlOsName/")
			}
		}

		listOf(
			"windows" to "Windows",
			"linux" to "Linux",
			"mac os x" to "macOs",
			"darwin" to "macOs",
			"osx" to "macOs"
		).forEach { (gradleOsName, pathName) ->
			it("writes the operating system name '$pathName' for '$gradleOsName' into the executable path") {
				mockPlatform(gradleOsName, X86_64)
				val testTask = createKubectlDownloadTestTask()

				expect(testTask.targetFile)
					.getAsPath()
					.contains(Path(pathName))
			}
		}

		it("uses the file extension '.exe' on windows") {
			mockPlatform("windows", X86_64)
			val testTask = createKubectlDownloadTestTask()

			expect(testTask.targetFile)
				.getAsPath()
				.extension.toBe("exe")
		}

		listOf("darwin", "linux").forEach { os ->
			it("uses no file extension on $os") {
				mockPlatform(os, X86_64)
				val testTask = createKubectlDownloadTestTask()

				expect(testTask.targetFile)
					.getAsPath()
					.extension.toBe("")
			}
		}

		it("finalizes ${KubectlExecutableDownloadTask::kubectlRelease.name} when running") {
			val testTask = createKubectlDownloadTestTask()
			testTask.kubectlRelease.set(V1_17_6)

			testTask.download()
			expect {
				testTask.kubectlRelease.set(V1_18_8)
			}.toThrow<IllegalStateException>().messageContains(KubectlExecutableDownloadTask::kubectlRelease.name, "final")
		}

		it("finalizes ${KubectlExecutableDownloadTask::targetFile.name} when running") {
			val testTask = createKubectlDownloadTestTask()
			testTask.targetFile.set(File("/first/file"))

			testTask.download()
			expect {
				testTask.targetFile.set(File("/new/file"))
			}.toThrow<IllegalStateException>().messageContains(KubectlExecutableDownloadTask::targetFile.name, "final")
		}
	}
})
