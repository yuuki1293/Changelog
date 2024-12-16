plugins {
    kotlin("jvm") version "2.0.20"
    id("com.github.yuuki1293.changelogmanager") version("1.0.0")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

changelog {
    file = file("${project.rootDir}/SAMPLE_CHANGELOG.md")
}

tasks.register("sample_task") {
    println("Changelog version: ${changelog.version.get()}")
    println("Changelog file: ${changelog.file.get()}")
    println("Changelog text: ${changelog.text()}")
    println("Changelog latest: ${changelog.latest()}")
}
