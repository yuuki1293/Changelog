package com.github.yuuki1293.changelog

import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

interface ChangelogExtension {
    /**
     * This is the target version. "latest" or any other version. ex: "1.0.0". default: "latest"
     */
    val version: Property<String>

    /**
     * This is the target file. default: File(project.rootDir, "CHANGELOG.md")
     */
    val file: Property<File>

    companion object {
        /**
         * The default version in use get.
         */
        private const val DEFAULT_VERSION: String = "latest"

        /**
         * The default changelog file name.
         */
        private const val DEFAULT_FILE_NAME: String = "CHANGELOG.md"

        fun setDefault(project: Project, extension: ChangelogExtension) {
            extension.version.convention(DEFAULT_VERSION)
            extension.file.convention(File(project.rootDir, DEFAULT_FILE_NAME))
        }
    }
}
