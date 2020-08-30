plugins {
    kotlin("jvm") version "1.4.0"
    id("com.palantir.git-version") version "0.12.3"
}

group = "de.joshuagleitze"
version = if (isSnapshot) versionDetails.gitHash else versionDetails.lastTag.drop("v")
status = if (isSnapshot) "snapshot" else "release"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

val Project.isSnapshot get() = versionDetails.commitDistance != 0

fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this

val Project.versionDetails get() = (this.extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails
