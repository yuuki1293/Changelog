pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Changelog"
include("plugin")

// ignore example-app while publishToMavenLocal
if(System.getenv("IGNORE_SAMPLE") != "1"){
    include("example-app")
}
