package net.xolt.freecam.publish.model

import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.Relationship
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.inputStream

data class ReleaseArtifact(
    val loader: String,
    val minecraftVersion: String,
    val gameVersions: List<String>,
    val javaVersions: List<String>,
    val relationships: List<Relationship>,
    val artifact: Path,
) {
    val size: Long by lazy {
        artifact.toFile().length()
    }

    val sha256: ByteArray by lazy {
        val digest = MessageDigest.getInstance("SHA-256")
        artifact.inputStream().use { input ->
            val buffer = ByteArray(0x2000)
            generateSequence { input.read(buffer) }
                .takeWhile { it > -1 }
                .forEach { len ->
                    digest.update(buffer, 0, len)
                }
        }
        digest.digest()
    }

    companion object
}

fun ReleaseArtifact.Companion.from(
    metadata: ProjectReleaseMetadata,
    artifactSupplier: (String) -> Path,
) = ReleaseArtifact(
    loader = metadata.loader,
    minecraftVersion = metadata.minecraft,
    gameVersions = metadata.gameVersions,
    javaVersions = metadata.javaVersions,
    relationships = metadata.relationships,
    artifact = artifactSupplier(metadata.filename),
)

fun Path.resolveArtifact(metadata: ProjectReleaseMetadata) =
    ReleaseArtifact.from(metadata, ::resolve)