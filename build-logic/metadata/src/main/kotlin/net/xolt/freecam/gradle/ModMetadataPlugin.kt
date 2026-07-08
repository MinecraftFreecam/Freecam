package net.xolt.freecam.gradle

import io.github.z4kn4fein.semver.Version
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.StaticModMetadata
import net.xolt.freecam.model.elaborate
import net.xolt.freecam.model.loadStaticMetadata
import net.xolt.freecam.util.withSuffix
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.add
import javax.inject.Inject

class ModMetadataPlugin(
    private val logger: Logger,
) : Plugin<Settings> {

    @Inject constructor() : this(
        logger = Logging.getLogger(ModMetadataPlugin::class.java),
    )

    companion object {
        const val EXTENSION_NAME = "meta"
    }

    override fun apply(settings: Settings) = with(settings) {
        val metadata = rootDir.resolve("metadata.toml").loadStaticMetadata().let {
            if (isReleaseBuild()) it else it.copy(
                buildDir = it.releaseVersion.toString().withSuffix("-SNAPSHOT"),
                version = addSnapshotSuffix(it.releaseVersion),
            )
        }

        logger.lifecycle(buildString {
            append("${metadata.name} version: ${metadata.releaseVersion}")
            if (metadata.releaseVersion != metadata.version) append(" (${metadata.version})")
        })

        extensions.add<StaticModMetadata>(EXTENSION_NAME, metadata)
        gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add<ModMetadata>(EXTENSION_NAME, metadata elaborate project)
            }
        }
    }

    private fun Settings.isReleaseBuild()
        = providers.gradleProperty("isReleaseBuild").orNull
        ?.trim()
        ?.takeUnless { it.isEmpty() }
        ?.lowercase()
        ?.let {
            when (it) {
                "true" -> true
                "false" -> false
                else -> error("Invalid `isReleaseBuild` value '$it' (expected 'true' or 'false')")
            }
        }
        ?: false

    private fun Settings.addSnapshotSuffix(version: Version) =
        version.addSnapshotSuffix(GitRepository(rootDir, GradleCommandRunner(providers)))

    internal fun Version.addSnapshotSuffix(repository: GitRepository) = copy(
        preRelease = buildString {
            preRelease
                ?.removeSuffix("-SNAPSHOT")
                ?.takeUnless { it.isBlank() || it == "SNAPSHOT" }
                ?.let {
                    append(it)
                    append('-')
                }

            append("SNAPSHOT")

            repository
                .resolveMetadata()
                .onFailure { e ->
                    logger.error("Could not resolve git metadata: ${e.message ?: e.javaClass.name}")
                }
                .onSuccess {
                    append('-')
                    append(it.revision)
                    if (it.isDirty) append("-dirty")
                }
        },
    )
}
