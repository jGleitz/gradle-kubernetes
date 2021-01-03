package de.joshuagleitze.test

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.writeText

/**
 * Offers access to a shared integration test Gradle project. The project is shared for all tests in the same Gradle project. This means
 * that tests can re-use the test project to improve performance, but also that the tests *cannot* be executed in parallel. However, since
 * different Gradle projects will use different test projects, tests from different Gradle projects *can* be executed in parallel.
 */
object GradleIntegrationTestProject {
	private val baseDir = Path("build").toAbsolutePath() / "test-outputs" / "[${GradleIntegrationTestProject::class.simpleName}]"
	val projectDir = baseDir / "testProject"
	val gradleHome = baseDir / "gradleHome"

	fun runGradle(vararg arguments: String, expectFailure: Boolean = false): BuildResult =
		GradleRunner.create()
			.withPluginClasspath()
			.forwardOutput()
			.withProjectDir(projectDir.toFile())
			.withTestKitDir(gradleHome.toFile())
			.withArguments(*arguments, "--stacktrace")
			.run {
				if (expectFailure) buildAndFail()
				else build()
			}

	fun prepare() {
		projectDir.clearIfExists()
		projectDir.createDirectories()
		gradleHome.createDirectories()
		(projectDir / "settings.gradle.kts").writeText(
			"""
			rootProject.name = "testProject"
			""".trimIndent()
		)
	}

	val integrationTestProject = this
}
