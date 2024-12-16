package com.github.yuuki1293.changelog

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.internal.impldep.org.apache.sshd.common.Property.IntegerProperty
import java.time.LocalDate

/**
 * Plugin extension
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ChangelogExtension(project: Project) {
    /**
     * This is the target version. "latest" or any other version. ex: "1.0.0". default: "latest"
     */
    val target: Property<String> = project.objects.property(String::class.java).convention(DEFAULT_VERSION)

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
    val allText: String get() = file.get().asFile.readText()

    /**
     * get latest data
     */
    val latest: Changelog.Data get() {
        sync()
        return changelog.getData()[0]
    }

    /**
     * get data set by the `version` property.
     *
     * NOTE: If you want to get target data, you can get them directly. use `changelog.text` instead of `changelog.data.text`
     */
    val data: Changelog.Data get() {
        return specific(target.get())
    }

    /**
     * get data.
     */
    fun specific(version: String): Changelog.Data {
        if(version == "latest") return latest

        sync()
        val target = changelog.getData().filter { it.version == version }
        when(target.size) {
            0 -> throw GradleException("$version version notfound")
            1 -> return target[0]
            else -> throw GradleException("There is multiple $version version available")
        }
    }

    val index: Int get() = data.index
    val version: String get() = data.version
    val date: LocalDate? get() = data.date
    val text: String get() = data.text
    val header: String get() = data.header
    val body: String get() = data.body

    /**
     * sync changelog instance.
     */
    private fun sync() {
        if (allText != changelog.rawText) {
            changelog = Changelog(allText, versionRegex.get(), versionIndex.get(), dateIndex.get(), pattern.get())
        }
    }
}
