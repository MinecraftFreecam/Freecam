package net.xolt.freecam.publish

import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.GitHubConfig
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.publish.model.resolveArtifact
import net.xolt.freecam.publish.platforms.GitHubPlatform
import net.xolt.freecam.publish.platforms.create
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

object DefaultPublisherFactory: PublisherFactory {
    override fun create(
        dryRun: Boolean,
        artifactsDir: Path,
        githubConfig: GitHubConfig
    ): Publisher = DefaultPublisher(
        artifactsDir = artifactsDir,
        github = GitHubPlatform.create(dryRun, githubConfig),
    )
}

data class DefaultPublisher(
    val artifactsDir: Path,
    val github: GitHubPlatform,
) : AutoCloseable, Publisher {

    override suspend fun publish(metadata: ReleaseMetadata) {
        val artifacts = artifactsDir.resolveArtifacts(metadata.versions).apply {
            verifyExists()
        }
        github.publishRelease(metadata, artifacts)
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

    override fun close() =
        sequenceOf(github)
            .mapNotNull { it as? AutoCloseable }
            .forEach(AutoCloseable::close)
}
