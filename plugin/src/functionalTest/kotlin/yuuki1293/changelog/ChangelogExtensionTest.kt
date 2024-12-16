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

    @Test fun `can get target changelog`() {
        // Set up the test build
        writeBasic()
        buildFile.appendText("""
            println(changelog.data())
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.withPluginClasspath()
        runner.forwardOutput()
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("""## [1.1.1] - 2023-03-05

### Added

- Arabic translation (#444).
- v1.1 French translation.
- v1.1 Dutch translation (#371).
- v1.1 Russian translation (#410).
- v1.1 Japanese translation (#363).
- v1.1 Norwegian Bokmål translation (#383).
- v1.1 "Inconsistent Changes" Turkish translation (#347).
- Default to most recent versions available for each languages.
- Display count of available translations (26 to date!).
- Centralize all links into `/data/links.json` so they can be updated easily.

### Fixed

- Improve French translation (#377).
- Improve id-ID translation (#416).
- Improve Persian translation (#457).
- Improve Russian translation (#408).
- Improve Swedish title (#419).
- Improve zh-CN translation (#359).
- Improve French translation (#357).
- Improve zh-TW translation (#360, #355).
- Improve Spanish (es-ES) transltion (#362).
- Foldout menu in Dutch translation (#371).
- Missing periods at the end of each change (#451).
- Fix missing logo in 1.1 pages.
- Display notice when translation isn't for most recent version.
- Various broken links, page versions, and indentations.

### Changed

- Upgrade dependencies: Ruby 3.2.1, Middleman, etc.

### Removed

- Unused normalize.css file.
- Identical links assigned in each translation file.
- Duplicate index file for the english version.

"""))
    }
}
