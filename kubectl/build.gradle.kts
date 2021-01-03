import de.undercouch.gradle.tasks.download.Download
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	`java-gradle-plugin`
	id("de.undercouch.download")
}

gradlePlugin {
	plugins {
		create("kubectl") {
			id = "${project.group}.kubectl"
			implementationClass = "de.joshuagleitze.gradle.kubectl.KubectlPlugin"
		}
	}
}

val generator by sourceSets.creating
val generatorImplementation = configurations[generator.implementationConfigurationName]
val generatorRuntime = configurations[generator.runtimeOnlyConfigurationName]
val data by sourceSets.creating
val dataImplementation = configurations[data.implementationConfigurationName]
val generated by sourceSets.creating
val generatedImplementation = configurations[generated.implementationConfigurationName]

dependencies {
	val ktorVersion = "1.5.4"

	implementation(data.output)
	api(generated.output)
	implementation(project(":common"))
	api(project(":kubernetes"))
	implementation(gradleKotlinDsl())
	implementation(name = "gradle-download-task", version = "4.1.1", group = "de.undercouch")
	implementation(name = "commons-io", version = "2.8.0", group = "commons-io")
	implementation(name = "string-notation", version = "1.4.0", group = "de.joshuagleitze")
	implementation(name = "kotlinx-serialization-json", group = "org.jetbrains.kotlinx", version = "1.2.1")
	implementation(name = "kaml", group = "com.charleskorn.kaml", version = "0.33.0")

	testImplementation(testFixtures(project(":common")))
	testImplementation(name = "kotest-runner-junit5", version = "4.3.2", group = "io.kotest")
	testImplementation(name = "atrium-fluent-en_GB", version = "0.16.0", group = "ch.tutteli.atrium")
	testImplementation(name = "atrium-gradle-testkit-fluent-en", version = "1.0.1", group = "de.joshuagleitze")
	testImplementation(name = "kotest-files", version = "2.0.0", group = "de.joshuagleitze")
	testImplementation(name = "mockk", version = "1.10.0", group = "io.mockk")

	generatorImplementation(data.output)
	generatorImplementation(kotlin("reflect"))
	generatorImplementation(name = "kotlinx-coroutines-core", version = "1.5.0", group = "org.jetbrains.kotlinx")
	generatorImplementation(name = "kotlinx-serialization-json", version = "1.2.1", group = "org.jetbrains.kotlinx")
	generatorImplementation(name = "kotlinpoet", version = "1.8.0", group = "com.squareup")
	generatorImplementation(name = "ktor-client-cio", version = ktorVersion, group = "io.ktor")
	generatorImplementation(name = "ktor-client-logging", version = ktorVersion, group = "io.ktor")
	generatorImplementation(name = "ktor-client-serialization", version = ktorVersion, group = "io.ktor")
	generatorImplementation(name = "jansi", version = "2.3.2", group = "org.fusesource.jansi")

	generatedImplementation(data.output)

	constraints {
		testImplementation(kotlin("reflect", version = KotlinCompilerVersion.VERSION))
	}
}

val compileDataKotlin by tasks.existing(KotlinCompile::class) {
	kotlinOptions {
		freeCompilerArgs += "-Xexplicit-api=strict"
	}
}

val compileGeneratorKotlin by tasks.existing(KotlinCompile::class) {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
		freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
		freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.FlowPreview"
		freeCompilerArgs += "-Xopt-in=io.ktor.util.KtorExperimentalAPI"
	}
}

val compileGeneratedKotlin by tasks.existing

val generateKubectlVersions by tasks.registering(JavaExec::class) {
	classpath = generator.runtimeClasspath
	mainClass.set("de.joshuagleitze.gradle.kubectl.generator.KubectlVersionsGenerator")
	systemProperties["jansi.force"] = true
	// compile the existing code first because it serves as a cache
	dependsOn(compileGeneratedKotlin)
	doFirst {
		args(
			generated.withConvention(KotlinSourceSet::class) { kotlin.sourceDirectories.first() },
			compileGeneratedKotlin.get().outputs.files.singleFile
		)
	}
}

kotlin {
	explicitApi()
}

tasks.compileKotlin {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
	}
}

tasks.compileTestKotlin {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
		freeCompilerArgs += "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
	}
}

val minikubeExecutable = buildDir.resolve("bin/minikube")
val downloadMinikube by tasks.registering(Download::class) {
	val os = DefaultNativePlatform.getCurrentOperatingSystem()
	val osName = when {
		os.isWindows -> "windows"
		os.isMacOsX -> "darwin"
		os.isLinux -> "linux"
		else -> error("cannot download minikube for $os!")
	}
	src("https://storage.googleapis.com/minikube/releases/latest/minikube-$osName-amd64")
	dest(minikubeExecutable)
	overwrite(false)
	doLast {
		minikubeExecutable.setExecutable(true)
	}
}

tasks.test {
	dependsOn(downloadMinikube)
	useJUnitPlatform {
		includeEngines("spek2")
	}
	systemProperty("minikube-executable", minikubeExecutable.absoluteFile)
}
