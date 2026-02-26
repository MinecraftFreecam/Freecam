package net.xolt.freecam.publish

import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.publish.model.resolveArtifact
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

object DefaultPublisherFactory: PublisherFactory {
    override fun create(
        dryRun: Boolean,
        artifactsDir: Path
    ): Publisher = DefaultPublisher(
        artifactsDir = artifactsDir,
    )
}

data class DefaultPublisher(
    val artifactsDir: Path,
) : AutoCloseable, Publisher {

    override suspend fun publish(metadata: ReleaseMetadata) {
        val artifacts = artifactsDir.resolveArtifacts(metadata.versions).apply {
            verifyExists()
        }
    }

    private fun Path.resolveArtifacts(metadata: List<ProjectReleaseMetadata>): List<ReleaseArtifact> =
        metadata
            .asSequence()
            .sorted()
            .map(::resolveArtifact)
            .toList()

    private fun List<ReleaseArtifact>.verifyExists() {
        filterNot { it.artifact.exists() }
            .takeUnless { it.isEmpty() }
            ?.joinToString("\n") { "- ${it.artifact.pathString}" }
            ?.let { files ->
                throw IllegalArgumentException("The following artifacts were not found:\n$files")
            }
    }

    override fun close() { }
}
