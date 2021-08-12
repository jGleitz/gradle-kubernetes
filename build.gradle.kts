plugins {
	kotlin("jvm") version "1.4.32" apply false
	id("com.palantir.git-version") version "0.12.3"
	id("com.gradle.plugin-publish") version "0.15.0" apply false
	`maven-publish`
}

group = "de.joshuagleitze.gradle.k8s"
version = if (isSnapshot) versionDetails.gitHash else versionDetails.lastTag.drop("v")
status = if (isSnapshot) "snapshot" else "release"

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

	afterEvaluate {
		publishing {
			publications.withType<MavenPublication> {
				pom {
					name.set("$groupId:$artifactId")
					if (extra.has("description")) description.set(extra["description"] as String)
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

			repositories.maven("https://maven.pkg.github.com/$githubRepository") {
				name = "GitHubPackages"
				credentials {
					username = githubOwner
					password = githubToken
				}
			}
		}
	}
}

val Project.isSnapshot get() = versionDetails.commitDistance != 0
fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this
val Project.versionDetails get() = (this.extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails
