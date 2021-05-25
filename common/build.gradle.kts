import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
	kotlin("jvm")
	`java-test-fixtures`
}

dependencies {
	implementation(name = "string-notation", version = "1.4.0", group = "de.joshuagleitze")

    testFixturesImplementation(gradleKotlinDsl())
    testFixturesImplementation(gradleApi())
    testFixturesImplementation(gradleTestKit())
    testFixturesImplementation(name = "atrium-fluent-en_GB", version = "0.16.0", group = "ch.tutteli.atrium")
    testFixturesImplementation(name = "kotest-framework-api-jvm", version = "4.6.0", group = "io.kotest")

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
