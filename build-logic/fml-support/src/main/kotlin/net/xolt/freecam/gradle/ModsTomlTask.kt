package net.xolt.freecam.gradle

import dev.eav.tomlkt.Toml
import dev.eav.tomlkt.encodeToNativeWriter
import net.xolt.freecam.gradle.dsl.ForgeModsTomlSpec
import net.xolt.freecam.gradle.dsl.ModsTomlSpec
import net.xolt.freecam.gradle.dsl.NeoForgeModsTomlSpec
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

private val serializer = Toml {
    explicitNulls = false
}

@CacheableTask
abstract class ForgeModsTomlTask @Inject constructor(objects: ObjectFactory): ModsTomlTask<ForgeModsTomlSpec>(
    fileName = "mods.toml",
    objects = objects,
) {
    public final override val toml = objects.newInstance(ForgeModsTomlSpec::class)
}

@CacheableTask
abstract class NeoForgeModsTomlTask @Inject constructor(objects: ObjectFactory): ModsTomlTask<NeoForgeModsTomlSpec>(
    fileName = "neoforge.mods.toml",
    objects = objects,
) {
    public final override val toml = objects.newInstance(NeoForgeModsTomlSpec::class)
}

sealed class ModsTomlTask<T : ModsTomlSpec<*, *>>(
    fileName: String,
    objects: ObjectFactory,
): DefaultTask() {

    @get:Nested
    protected abstract val toml: T

    @get:OutputFile
    val outputFile = objects.fileProperty().convention(project.layout.buildDirectory.dir("generated/mods-toml").map { it.file(fileName) })

    init {
        group = "mod development/internal"
    }

    fun toml(action: T.() -> Unit) {
        toml.apply(action)
    }

    @TaskAction
    fun generate() {
        val model = toml.toModel()
        outputFile.asFile.get().apply {
            parentFile.mkdirs()
            bufferedWriter().use { writer ->
                serializer.encodeToNativeWriter(model, writer)
            }
        }
    }
}
