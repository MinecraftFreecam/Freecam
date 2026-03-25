package net.xolt.freecam.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named

interface LoomAdapterExtension {
    val hasMappings: Boolean
    val modJar: TaskProvider<Jar>
    val modSourcesJar: TaskProvider<Jar>
}

@JvmInline
internal value class RemapLoomAdapterExtension(val project: Project) : LoomAdapterExtension {

    override val hasMappings: Boolean
        get() = true

    override val modJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>("remapJar")

    override val modSourcesJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>("remapSourcesJar")
}

@JvmInline
internal value class NoRemapLoomAdapterExtension(val project: Project) : LoomAdapterExtension {

    override val hasMappings: Boolean
        get() = false

    override val modJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)

    override val modSourcesJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>("sourcesJar")
}
