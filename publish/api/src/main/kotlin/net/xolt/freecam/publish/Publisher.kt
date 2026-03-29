package net.xolt.freecam.publish

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.GitHubConfig
import java.nio.file.Path

interface Publisher : AutoCloseable {
    suspend fun publish(metadata: ReleaseMetadata)
}

interface PublisherFactory {
    fun create(
        dryRun: Boolean,
        artifactsDir: Path,
        githubConfig: GitHubConfig,
    ): Publisher
}