package yuuki1293.changelog

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import java.io.File

abstract class ChangelogTask : DefaultTask() {
    @get:InputFile
    val file: File = File("CHANGELOG.md")
}
