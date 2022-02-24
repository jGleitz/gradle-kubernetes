import org.jetbrains.dokka.gradle.DokkaTask

plugins {
	kotlin("jvm") version "1.6.10" apply false
	id("org.jetbrains.dokka") version "1.6.0"
	id("com.palantir.git-version") version "0.12.3"
	id("com.gradle.plugin-publish") version "0.15.0"
	`maven-publish`
	signing
}

group = "de.joshuagleitze.gradle.k8s"
version = if (isSnapshot) versionDetails.gitHash else versionDetails.lastTag.drop("v")
status = if (isSnapshot) "snapshot" else "release"
val gitRef = if (isSnapshot) versionDetails.gitHash else versionDetails.lastTag

allprojects {
	repositories {
		mavenCentral()
	}
}

val githubRepository: String? by project
val githubOwner = githubRepository?.split("/")?.get(0)
val githubToken: String? by project

subprojects {
	group = rootProject.group
	version = rootProject.version
	status = rootProject.status

	apply {
		plugin("org.gradle.maven-publish")
		plugin("org.gradle.signing")
	}

	signing {
		val signingKey: String? by project
		val signingKeyPassword: String? by project
		useInMemoryPgpKeys(signingKey, signingKeyPassword)
	}

	afterEvaluate {
		val projectDescription = extra["description"] as String

		pluginBundle {
			website = "https://github.com/$githubRepository"
			vcsUrl = "https://github.com/$githubRepository"
		}

		tasks.withType<DokkaTask> {
			dokkaSourceSets.named("main") {
				this.DokkaSourceSetID(if (extra.has("artifactId")) extra["artifactId"] as String else project.name)
				sourceLink {
					val projectPath = projectDir.absoluteFile.relativeTo(rootProject.projectDir.absoluteFile)
					localDirectory.set(file("src/main/kotlin"))
					remoteUrl.set(uri("https://github.com/$githubRepository/blob/$gitRef/$projectPath/src/main/kotlin").toURL())
					remoteLineSuffix.set("#L")
				}
			}
		}

		publishing {
			publications.withType<MavenPublication> {
				signing.sign(this)

				pom {
					name.set("$groupId:$artifactId")
					description.set(projectDescription)
					inceptionYear.set("2020")
					url.set("https://github.com/$githubRepository")
					ciManagement {
						system.set("GitHub Actions")
						url.set("https://github.com/$githubRepository/actions")
					}
					issueManagement {
						system.set("GitHub Issues")
						url.set("https://github.com/$githubRepository/issues")
					}
					developers {
						developer {
							name.set("Joshua Gleitze")
							email.set("dev@joshuagleitze.de")
						}
					}
					scm {
						connection.set("scm:git:https://github.com/$githubRepository.git")
						developerConnection.set("scm:git:git://git@github.com:$githubRepository.git")
						url.set("https://github.com/$githubRepository")
					}
					licenses {
						license {
							name.set("MIT")
							url.set("https://opensource.org/licenses/MIT")
							distribution.set("repo")
						}
					}
				}
			}

			val githubPackages = repositories.maven("https://maven.pkg.github.com/$githubRepository") {
				name = "GitHubPackages"
				credentials {
					username = githubOwner
					password = githubToken
				}
			}

			val publishToGithub = tasks.named("publishAllPublicationsTo${githubPackages.name.capitalize()}Repository")
			val publishPlugins by tasks

			tasks.register("release") {
				group = "release"
				description = "Releases the project to all remote repositories"
				dependsOn(publishToGithub, publishPlugins)
			}
		}
	}
}

val Project.isSnapshot get() = versionDetails.commitDistance != 0
fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this
val Project.versionDetails get() = (this.extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails
