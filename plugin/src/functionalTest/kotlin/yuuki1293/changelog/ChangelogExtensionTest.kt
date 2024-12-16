package yuuki1293.changelog

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("FunctionName")
class ChangelogExtensionTest {
    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }
    private val testFile by lazy { projectDir.resolve("CHANGELOG.md") }

    private fun writeBasic() {
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.github.yuuki1293.changelog')
            }
            
            changelog {
                version = "1.1.1"
            }
        """.trimIndent())
        testFile.writeText(this::class.java.getResource("/SAMPLE_CHANGELOG.md")?.readText() ?: "")
    }

    @Test fun `can get latest changelog`() {
        // Set up the test build
        writeBasic()
        buildFile.appendText("""
            println(changelog.latest())
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.withPluginClasspath()
        runner.forwardOutput()
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("""## [Unreleased]

### Added

- v1.1 Brazilian Portuguese translation.
- v1.1 German Translation
- v1.1 Spanish translation.
- v1.1 Italian translation.
- v1.1 Polish translation.
- v1.1 Ukrainian translation.

### Changed

- Use frontmatter title & description in each language version template
- Replace broken OpenGraph image with an appropriately-sized Keep a Changelog
  image that will render properly (although in English for all languages)
- Fix OpenGraph title & description for all languages so the title and
  description when links are shared are language-appropriate

### Removed

- Trademark sign previously shown after the project description in version
  0.3.0

"""))
    }
}
