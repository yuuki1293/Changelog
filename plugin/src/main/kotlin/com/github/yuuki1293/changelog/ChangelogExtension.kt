package com.github.yuuki1293.changelog

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

open class ChangelogExtension(project: Project) {
    /**
     * This is the target version. "latest" or any other version. ex: "1.0.0". default: "latest"
     */
    @Suppress("unused")
    val version: Property<String> = project.objects.property(String::class.java).convention(DEFAULT_VERSION)

    /**
     * This is the target file. default: File(project.rootDir, "CHANGELOG.md")
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val file: RegularFileProperty = project.objects.fileProperty().convention(project.rootProject.layout.projectDirectory.file(DEFAULT_FILE_NAME))

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

    /**
     * get all raw text.
     */
    fun text(): String = file.get().asFile.readText()
}
