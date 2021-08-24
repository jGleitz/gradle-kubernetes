@file:Suppress("SuspiciousCollectionReassignment")

import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
	kotlin("jvm")
	`java-gradle-plugin`
	`java-test-fixtures`
	id("com.gradle.plugin-publish")
	id("org.jetbrains.dokka")
}

val artifactId by extra("kubernetes")
val description by extra("Declare Kubernetes clusters for deployments in Gradle.")

gradlePlugin {
	plugins {
		create("kubernetes") {
			id = "de.joshuagleitze.kubernetes"
			displayName = "kubernetes"
			description = "Allows declaring kubernetes clusters so other plugins can deploy to them."
			implementationClass = "de.joshuagleitze.gradle.kubernetes.KubernetesPlugin"
		}
	}
}

pluginBundle {
	tags = listOf("kubernetes", "k8s", "cloud")
}

dependencies {
	implementation(gradleKotlinDsl())
	implementation(name = "string-notation", version = "1.4.0", group = "de.joshuagleitze")

	testFixturesImplementation(gradleKotlinDsl())
	testFixturesImplementation(gradleApi())
	testFixturesImplementation(gradleTestKit())
	testFixturesImplementation(name = "atrium-fluent-en_GB", version = "0.16.0", group = "ch.tutteli.atrium")
	testFixturesImplementation(name = "kotest-framework-api-jvm", version = "4.6.2", group = "io.kotest")

	testImplementation(name = "kotest-runner-junit5", version = "4.6.2", group = "io.kotest")
	testImplementation(name = "atrium-fluent-en_GB", version = "0.16.0", group = "ch.tutteli.atrium")
	testImplementation(name = "atrium-gradle-testkit-fluent-en", version = "1.0.1", group = "de.joshuagleitze")
	testImplementation(name = "kotest-files", version = "2.0.0", group = "de.joshuagleitze")
	testImplementation(name = "mockk", version = "1.12.0", group = "io.mockk")

    constraints {
        testImplementation(kotlin("reflect", version = KotlinCompilerVersion.VERSION))
    }
}

kotlin {
	explicitApi()
}

tasks.compileKotlin {
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

tasks.compileTestKotlin {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
		freeCompilerArgs += "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
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

tasks.test {
	useJUnitPlatform()
}
