package net.xolt.freecam.publish.platforms

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.logging.Logger
import net.xolt.freecam.publish.model.ReleaseArtifact

interface CurseForgePlatform : Platform { companion object }

fun CurseForgePlatform.Companion.create(dryRun: Boolean = false) =
    if (dryRun) DryRunCurseForgePlatform()
    else DefaultCurseForgePlatform()

internal class DryRunCurseForgePlatform : CurseForgePlatform {
    private val logger = Logger.scoped("dry-run").scoped("CurseForge")

    override suspend fun publishRelease(metadata: ReleaseMetadata, artifacts: List<ReleaseArtifact>) {
        artifacts.forEach {
            logger.info { "${metadata.modVersion} upload ${it.artifact}" }
        }
    }
}

internal class DefaultCurseForgePlatform : CurseForgePlatform {
    private val logger = Logger.scoped("CurseForge")

    override suspend fun publishRelease(metadata: ReleaseMetadata, artifacts: List<ReleaseArtifact>) {
        TODO("Not yet implemented")
    }
}
