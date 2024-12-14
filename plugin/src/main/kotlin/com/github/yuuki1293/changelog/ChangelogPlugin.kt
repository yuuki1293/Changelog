package com.github.yuuki1293.changelog

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin root.
 */
class ChangelogPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("changelog", ChangelogExtension::class.java)
        ChangelogExtension.setDefault(project, extension)
    }
}
