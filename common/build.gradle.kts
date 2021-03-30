import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
	kotlin("jvm")
	`java-test-fixtures`
}

dependencies {
	val spekVersion = "2.0.15"

	implementation(name = "string-notation", version = "1.4.0", group = "de.joshuagleitze")

	testFixturesImplementation(gradleKotlinDsl())
	testFixturesImplementation(gradleApi())
	testFixturesImplementation(gradleTestKit())
	testFixturesImplementation(name = "spek-dsl-jvm", version = spekVersion, group = "org.spekframework.spek2")
	testFixturesImplementation(name = "atrium-fluent-en_GB", version = "0.16.0", group = "ch.tutteli.atrium")

	constraints {
		testFixturesImplementation(kotlin("reflect", version = KotlinCompilerVersion.VERSION))
	}
}

tasks.compileTestFixturesKotlin {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
		freeCompilerArgs += "-Xopt-in=ch.tutteli.atrium.api.fluent.en_GB.ExperimentalWithOptions"
		freeCompilerArgs += "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
	}
}
