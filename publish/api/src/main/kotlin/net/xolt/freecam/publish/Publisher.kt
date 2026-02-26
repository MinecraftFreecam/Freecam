package net.xolt.freecam.publish

import net.xolt.freecam.model.ReleaseMetadata
import java.nio.file.Path

interface Publisher : AutoCloseable {
    suspend fun publish(metadata: ReleaseMetadata)
}

interface PublisherFactory {
    fun create(
        dryRun: Boolean,
        artifactsDir: Path,
    ): Publisher
}