package com.github.yuuki1293.changelog

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

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
        val specifics = changelog.getData().filter { it.version == version.get() }
        when(specifics.size) {
            0 -> throw GradleException("${version.get()} version notfound")
            1 -> return specifics[0]
            else -> throw GradleException("There is multiple ${version.get()} version available")
        }
    }

    /**
     * sync changelog instance.
     */
    private fun sync() {
        if (text() != changelog.rawText) {
            val versionRegex = Changelog.DEFAULT_VERSION_REGEX
            val versionIndex = Changelog.DEFAULT_VERSION_INDEX
            val dateIndex = Changelog.DEFAULT_DATE_INDEX
            changelog = Changelog(text(), versionRegex, dateIndex, versionIndex)
        }
    }
}
