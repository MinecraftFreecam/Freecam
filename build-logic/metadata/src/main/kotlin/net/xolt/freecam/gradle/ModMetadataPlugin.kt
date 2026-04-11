package net.xolt.freecam.gradle

import dev.eav.tomlkt.Toml
import kotlinx.serialization.decodeFromString
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.StaticModMetadata
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.add

class ModMetadataPlugin : Plugin<Settings> {

    companion object {
        const val EXTENSION_NAME = "meta"
    }

    override fun apply(settings: Settings) = with(settings) {
        // Load StaticModMetadata ourselves, rather than using stonecutter centralized properties
        val file = rootDir.resolve("metadata.toml")
        val toml = file.readText()
        val metadata = Toml.decodeFromString<MetadataToml>(toml).mod

        extensions.add<StaticModMetadata>(EXTENSION_NAME, metadata)
        gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add<ModMetadata>(EXTENSION_NAME, ProjectModMetadata(this, metadata))
            }
        }
    }
}