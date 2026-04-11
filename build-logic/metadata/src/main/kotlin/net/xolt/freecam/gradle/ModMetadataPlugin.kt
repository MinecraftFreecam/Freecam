package net.xolt.freecam.gradle

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
        val metadata = rootDir.resolve("metadata.toml").loadStaticMetadata()

        extensions.add<StaticModMetadata>(EXTENSION_NAME, metadata)
        gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add<ModMetadata>(EXTENSION_NAME, metadata elaborate project)
            }
        }
    }
}