import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
	kotlin("jvm")
	`java-gradle-plugin`
}

gradlePlugin {
	plugins {
		create("kubernetes") {
			id = "${project.group}.kubernetes"
			implementationClass = "de.joshuagleitze.gradle.kubernetes.KubernetesPlugin"
		}
	}
}

dependencies {
	val spekVersion = "2.0.15"

	implementation(gradleKotlinDsl())
	implementation(project(":common"))
	implementation(name = "string-notation", version = "1.4.0", group = "de.joshuagleitze")

	testImplementation(testFixtures(project(":common")))
	testImplementation(name = "spek-dsl-jvm", version = spekVersion, group = "org.spekframework.spek2")
	testImplementation(name = "atrium-fluent-en_GB", version = "0.16.0", group = "ch.tutteli.atrium")
	testImplementation(name = "atrium-gradle-testkit-fluent-en", version = "1.0.1", group = "de.joshuagleitze")
	testImplementation(name = "spek-testfiles", version = "2.0.0", group = "de.joshuagleitze")
	testImplementation(name = "mockk", version = "1.11.0", group = "io.mockk")
	testRuntimeOnly(name = "spek-runner-junit5", version = spekVersion, group = "org.spekframework.spek2")

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

tasks.test {
	useJUnitPlatform {
		includeEngines("spek2")
	}
}
