package net.xolt.freecam.publish.platforms

import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.model.ReleaseArtifact

interface Platform {
    suspend fun publishRelease(metadata: ReleaseMetadata, artifacts: List<ReleaseArtifact>)
}
