# Changelog
gradle plugin for read changelog. Retrieve specific version text and dates from Changelog.

## Usage
```groovy
plugins {
    // build.gradle
    id 'io.github.yuuki1293.changelog' version '1.0.0'

    // build.gradle.kts
    id("io.github.yuuki1293.changelog") version ("1.1.0")
}

// configuration (Optional)
changelog {
    file = file("${project.rootDir}/SAMPLE_CHANGELOG.md") // The changelog file. (Optional)
    target = "1.1.1" // target version (Optional)
}
```

See [example](example-app/build.gradle.kts).
