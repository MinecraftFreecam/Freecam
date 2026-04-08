package net.xolt.freecam.gradle

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import net.fabricmc.loom.util.Constants.Configurations as LoomConfigurations

interface LoomAdapterExtension {
    val hasMappings: Boolean
    val modJar: TaskProvider<Jar>
    val modSourcesJar: TaskProvider<Jar>

    /**
     * Apply Mojang's official mappings to the `mappings` configuration,
     * if this loom version does remapping.
     */
    fun applyMojangMappings(): Dependency?
}

@JvmInline
internal value class RemapLoomAdapterExtension(val project: Project) : LoomAdapterExtension {

    private val loom: LoomGradleExtensionAPI
        get() = project.extensions.getByType()

    override val hasMappings: Boolean
        get() = true

    override val modJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>("remapJar")

    override val modSourcesJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>("remapSourcesJar")

    override fun applyMojangMappings() = project.dependencies.add(
        LoomConfigurations.MAPPINGS,
        loom.officialMojangMappings(),
    )
}

@JvmInline
internal value class NoRemapLoomAdapterExtension(val project: Project) : LoomAdapterExtension {

    override val hasMappings: Boolean
        get() = false

    override val modJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)

    override val modSourcesJar: TaskProvider<Jar>
        get() = project.tasks.named<Jar>("sourcesJar")

    override fun applyMojangMappings() = null
}
