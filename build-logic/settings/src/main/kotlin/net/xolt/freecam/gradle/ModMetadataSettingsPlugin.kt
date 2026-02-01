package net.xolt.freecam.gradle

import dev.eav.tomlkt.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.ModMetadataToml
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.add

@Serializable
private data class MetadataToml(
    val mod: ModMetadataToml,
)

class ModMetadataSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val name = "meta"
        val file = settings.rootDir.resolve("metadata.toml")
        val toml = file.readText()
        val metadata = Toml.decodeFromString<MetadataToml>(toml)

        settings.extensions.add<ModMetadata>(name, metadata.mod)
        settings.gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add(name, metadata.mod)
            }
        }
    }
}
