import ch.tutteli.atrium.api.fluent.en_GB.cause
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.isReadable
import ch.tutteli.atrium.api.fluent.en_GB.messageContains
import ch.tutteli.atrium.api.fluent.en_GB.notToThrow
import ch.tutteli.atrium.api.fluent.en_GB.resolve
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gradle.kubectl.KubectlDownloadTask
import de.joshuagleitze.gradle.kubectl.KubectlExtension
import de.joshuagleitze.test.gradle.output
import de.joshuagleitze.test.gradle.task
import de.joshuagleitze.test.gradle.wasSuccessful
import de.joshuagleitze.test.spek.testfiles.testFiles
import org.gradle.api.GradleException
import org.gradle.api.internal.provider.ValueSupplier.ValueProducer.task
import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.Architectures.ARM_V7
import org.gradle.nativeplatform.platform.internal.Architectures.IA_64
import org.gradle.nativeplatform.platform.internal.Architectures.X86
import org.gradle.nativeplatform.platform.internal.Architectures.X86_64
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.time.seconds

object KubectlDownloadTaskSpec: Spek({
	val testFiles = testFiles()

	describe("kubectlDownload task") {
		describe("architecture check") {
			val testProject by memoized { ProjectBuilder.builder().build() }
			beforeEachTest { KubectlExtension.register(testProject) }

			listOf(X86, IA_64, ARM_V7).forEach { architecture ->
				it("rejects being registered on ${architecture.canonicalName}") {
					val testPlatform = DefaultNativePlatform("test", DefaultOperatingSystem("linux"), Architectures.of(architecture))
					expect {
						KubectlDownloadTask.register(testProject, testPlatform).get()
					}.toThrow<GradleException>().cause<IllegalStateException> {
						messageContains(architecture.canonicalName)
					}
				}
			}

			it("can be registered on amd64") {
				val testPlatform = DefaultNativePlatform("test", DefaultOperatingSystem("linux"), Architectures.of(X86_64))
				expect {
					KubectlDownloadTask.register(testProject, testPlatform).get()
				}.notToThrow()
			}
		}

		describe("operating system check") {
			val testProject by memoized { ProjectBuilder.builder().build() }
			beforeEachTest { KubectlExtension.register(testProject) }

			listOf("sunos", "solaris", "freebsd").forEach { os ->
				it("rejects being registered on $os") {
					val testPlatform = DefaultNativePlatform("test", DefaultOperatingSystem(os), Architectures.of(X86_64))
					expect {
						KubectlDownloadTask.register(testProject, testPlatform).get()
					}.toThrow<GradleException>().cause<IllegalStateException> {
						messageContains(os)
					}
				}
			}

			listOf("windows", "linux", "mac os x", "darwin", "osx").forEach { os ->
				it("can be registered on $os") {
					val testPlatform = DefaultNativePlatform("test", DefaultOperatingSystem(os), Architectures.of(X86_64))
					expect {
						KubectlDownloadTask.register(testProject, testPlatform).get()
					}.notToThrow()
				}
			}
		}

		it("downloads kubectl to build/bin", timeout = 60.seconds.toLongMilliseconds()) {
			val projectDir = testFiles.createDirectory("testProject")
			projectDir.resolve("build.gradle.kts").toFile().writeText(
				"""
				plugins {
					id("de.joshuagleitze.kubectl")
				}
			""".trimIndent()
			)
			projectDir.resolve("settings.gradle.kts").toFile().writeText(
				"""
				rootProject.name = "testProject"
			""".trimIndent()
			)

			val buildResult = GradleRunner.create()
				.withPluginClasspath()
				.forwardOutput()
				.withProjectDir(projectDir.toFile())
				.withArguments(KubectlDownloadTask.NAME)
				.build()

			val executableName = if (System.getProperty("os.name").contains("Windows")) "kubectl.exe" else "kubectl"
			expect(buildResult) {
				task(":${KubectlDownloadTask.NAME}").wasSuccessful()
				output.contains(executableName)
			}

			// TODO: check that it is executable!
			expect(projectDir).resolve("build/bin/$executableName").isReadable()
		}
	}
})
