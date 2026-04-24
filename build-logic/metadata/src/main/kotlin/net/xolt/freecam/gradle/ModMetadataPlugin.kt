package net.xolt.freecam.gradle

import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.StaticModMetadata
import net.xolt.freecam.model.elaborate
import net.xolt.freecam.model.loadStaticMetadata
import net.xolt.freecam.util.withSuffix
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.add

class ModMetadataPlugin : Plugin<Settings> {

    companion object {
        const val EXTENSION_NAME = "meta"
    }

    private val logger = Logging.getLogger(ModMetadataPlugin::class.java)

    override fun apply(settings: Settings) = with(settings) {
        val isRelease = providers.gradleProperty("isReleaseBuild")
            .map { it.equals("true", ignoreCase = true) }
            .getOrElse(false)

        val metadata = rootDir.resolve("metadata.toml").loadStaticMetadata().let {
            if (isRelease) it else it.copy(version = it.version.withSuffix("-SNAPSHOT"))
        }

        logger.lifecycle("${metadata.name} version: ${metadata.version}")

        extensions.add<StaticModMetadata>(EXTENSION_NAME, metadata)
        gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add<ModMetadata>(EXTENSION_NAME, metadata elaborate project)
            }
        }
    }
}
