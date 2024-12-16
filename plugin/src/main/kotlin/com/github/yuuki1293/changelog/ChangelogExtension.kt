package com.github.yuuki1293.changelog

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.internal.impldep.org.apache.sshd.common.Property.IntegerProperty

/**
 * Plugin extension
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ChangelogExtension(project: Project) {
    /**
     * This is the target version. "latest" or any other version. ex: "1.0.0". default: "latest"
     */
    val version: Property<String> = project.objects.property(String::class.java).convention(DEFAULT_VERSION)

    /**
     * This is the target file. default: File(project.rootDir, "CHANGELOG.md")
     */
    val file: RegularFileProperty =
        project.objects.fileProperty().convention(project.rootProject.layout.projectDirectory.file(DEFAULT_FILE_NAME))

    /**
     * This is used to locate the version header.
     *
     * default: Regex("""^##\s+\[\s*(.+)\s*]\s*(-\s*(\d{4}-\d{2}-\d{2}))?""")
     *
     *  Match these:
     *  - `## [Unreleased]`
     *  - `## [1.1.1] - 2023-03-05`
     *  - `## [ 1.1.0 ]-2019-02-15`
     *
     *  Example. If you use yyyy/MM/dd.
     *  ```
     *  changelog {
     *      versionRegex = Regex("""^##\s+\[\s*(.+)\s*]\s*(-\s*(\d{4}/\d{2}/\d{2}))?""")
     *  }
     *  ```
     */
    val versionRegex: Property<Regex> = project.objects.property(Regex::class.java).convention(Changelog.DEFAULT_VERSION_REGEX)

    /**
     * Which occurrence of version matches the regular expression? default: 1
     *
     * [versionRegex] is the default. `## [1.1.0] - 2019-02-15` matches like this.
     * - 0: `## [1.1.0] - 2019-02-15`
     * - 1: `1.1.0`
     * - 2: ` - 2019-02-15`
     * - 3: `2019-02-15`
     */
    val versionIndex: Property<Int> = project.objects.property(Int::class.java).convention(Changelog.DEFAULT_VERSION_INDEX)

    /**
     * Which occurrence of date matches the regular expression? default: 3
     *
     * NOTE: Must be parsable `LocalDate.parse(dateStr, DateTimeFormatter.ofPattern([pattern]))`
     *
     * [versionRegex] is the default. `## [1.1.0] - 2019-02-15` matches like this.
     * - 0: `## [1.1.0] - 2019-02-15`
     * - 1: `1.1.0`
     * - 2: ` - 2019-02-15`
     * - 3: `2019-02-15`
     */
    val dateIndex: Property<Int> = project.objects.property(Int::class.java).convention(Changelog.DEFAULT_DATE_INDEX)

    /**
     * Patterns used by date parser. default "yyyy-MM-dd"
     */
    val pattern: Property<String> = project.objects.property(String::class.java).convention(Changelog.DEFAULT_PATTERN)

    companion object {
        /**
         * The default version in use get.
         */
        private const val DEFAULT_VERSION: String = "latest"

        /**
         * The default changelog file name.
         */
        private const val DEFAULT_FILE_NAME: String = "CHANGELOG.md"
    }

    private var changelog: Changelog = Changelog.EMPTY

    /**
     * get all raw text.
     */
    fun text(): String = file.get().asFile.readText()

    /**
     * get latest data
     */
    fun latest(): Changelog.Data {
        sync()
        return changelog.getData()[0]
    }

    /**
     * get data set by the `version` property.
     */
    fun data(): Changelog.Data {
        if(version.get() == "latest") return latest()

        sync()
        val target = changelog.getData().filter { it.version == version.get() }
        when(target.size) {
            0 -> throw GradleException("${version.get()} version notfound")
            1 -> return target[0]
            else -> throw GradleException("There is multiple ${version.get()} version available")
        }
    }

    /**
     * sync changelog instance.
     */
    private fun sync() {
        if (text() != changelog.rawText) {
            changelog = Changelog(text(), versionRegex.get(), versionIndex.get(), dateIndex.get(), pattern.get())
        }
    }
}
