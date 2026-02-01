package net.xolt.freecam.gradle

import dev.eav.tomlkt.Toml
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.ModMetadataToml
import net.xolt.freecam.model.StaticModMetadata
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.findByType

@Serializable
private data class MetadataToml(
    val mod: ModMetadataToml,
)

private class ProjectModMetadata(
    private val project: Project,
    private val meta: StaticModMetadata,
)
: StaticModMetadata by meta, ModMetadata
{
    private val sc get() = project.extensions.findByType<StonecutterBuildExtension>()

    override val mc: String
        get() = requireNotNull(sc) {
            "${project.path} without `stonecutter` extension cannot read `mc` "
        }.current.version

    override val loader: String
        get() = requireNotNull(sc) {
            "${project.path} without `stonecutter` extension cannot read `loader` "
        }.branch.id
}

class ModMetadataSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val name = "meta"
        val file = settings.rootDir.resolve("metadata.toml")
        val toml = file.readText()
        val metadata = Toml.decodeFromString<MetadataToml>(toml).mod

        settings.extensions.add<StaticModMetadata>(name, metadata)
        settings.gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add<ModMetadata>(name, ProjectModMetadata(this, metadata))
            }
        }
    }
}
