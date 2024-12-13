plugins {
    kotlin("jvm") version "2.0.20"
}

buildscript {
    repositories {
        maven {
            name = "Local"
            url = uri("../build/repo")
        }
    }
    dependencies {
        classpath("ChangelogManager:plugin:1.0.0")
    }
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
