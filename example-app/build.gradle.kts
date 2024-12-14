import com.github.yuuki1293.changelog.ChangelogManagerPlugin

plugins {
    kotlin("jvm") version "2.0.20"
}

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("ChangelogManager:plugin:1.0.0")
    }
}

group = "org.example"
version = "unspecified"

repositories {
    mavenLocal()
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

apply<ChangelogManagerPlugin>()
