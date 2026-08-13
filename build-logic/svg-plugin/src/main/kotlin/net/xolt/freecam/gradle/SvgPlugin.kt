package net.xolt.freecam.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

abstract class SvgPlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "svg"
    }

    override fun apply(project: Project): Unit = with(project) {
        extensions.create<SvgPluginExtension>(EXTENSION_NAME, this)
    }
}
