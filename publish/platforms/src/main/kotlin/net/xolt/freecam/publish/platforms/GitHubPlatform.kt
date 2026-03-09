package net.xolt.freecam.publish.platforms

import io.ktor.http.*
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.model.ReleaseType
import net.xolt.freecam.publish.http.GitHubRelease
import net.xolt.freecam.publish.http.GitHubReleaseAsset
import net.xolt.freecam.publish.http.GitHubReleasesClient
import net.xolt.freecam.publish.http.JavaArchive
import net.xolt.freecam.publish.logging.Logger
import net.xolt.freecam.publish.model.GitHubConfig
import net.xolt.freecam.publish.model.ReleaseArtifact
import kotlin.io.path.name
import kotlin.io.path.readBytes

interface GitHubPlatform : Platform { companion object }

fun GitHubPlatform.Companion.create(dryRun: Boolean = false, config: GitHubConfig) =
    if (dryRun) DryRunGitHubPlatform(config)
    else DefaultGitHubPlatform(config)

internal class DryRunGitHubPlatform(
    private val config: GitHubConfig,
    private val logger: Logger = Logger.scoped("dry-run").scoped("GitHub"),
) : GitHubPlatform {

    override suspend fun publishRelease(metadata: ReleaseMetadata, artifacts: List<ReleaseArtifact>) {
        logger.info { "using config $config" }
        logger.info { "creating release ${metadata.platforms.github.tag}" }
        logger.trace { "metadata: $metadata" }

        artifacts.forEach {
            logger.info { "uploading ${it.artifact}" }
            logger.debug { "${it.size} bytes" }
            logger.debug { "SHA-256: ${it.sha256.toHexString()}" }
        }
    }
}

internal class DefaultGitHubPlatform(
    private val config: GitHubConfig,
    private val client: GitHubReleasesClient = GitHubReleasesClient(config),
    private val logger: Logger = Logger.scoped("GitHub"),
) : GitHubPlatform, AutoCloseable {

    override suspend fun publishRelease(
        metadata: ReleaseMetadata,
        artifacts: List<ReleaseArtifact>,
    ) {
        // Get the release
        val release = reconcileRelease(metadata)

        // Upload assets
        reconcileAssets(release, artifacts).forEach {
            uploadAsset(release, it)
        }

        // Finally, undraft
        if (release.draft) {
            logger.info { "un-drafting ${release.tagName}" }
            client.updateDraftState(release, false)
            logger.trace { "un-drafted" }
        }
    }

    internal suspend fun reconcileRelease(
        metadata: ReleaseMetadata,
        draft: Boolean = true,
    ): GitHubRelease {
        val tag = metadata.platforms.github.tag
        logger.trace { "reconciling $tag" }

        val prerelease = metadata.releaseType != ReleaseType.RELEASE
        logger.trace {
            val isOrIsNot = if (prerelease) "is" else "isn't"
            "computed ${metadata.releaseType} $isOrIsNot a prerelease"
        }

        // Try to fetch existing release by tag
        logger.info { "looking up release $tag" }
        val existingId = client.getReleaseIDByTag(tag)

        val result = if (existingId == null) {
            logger.info { "creating a new release" }
            client.createRelease(
                tagName = tag,
                targetCommitish = config.headSha,
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = prerelease,
                draft = draft,
            )
        } else {
            logger.info { "updating existing release" }
            logger.debug { "existing release has ID $existingId"}
            client.updateRelease(
                releaseId = existingId,
                tagName = tag,
                targetCommitish = config.headSha,
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = prerelease,
            )
        }

        logger.debug { if (existingId == null) "created $tag" else "updated $tag" }
        logger.trace { "result: $result" }
        return result
    }

    internal suspend fun reconcileAssets(
        release: GitHubRelease,
        artifacts: List<ReleaseArtifact>,
    ): List<ReleaseArtifact> = buildList {
        logger.trace { "reconciling assets for ${release.id}" }

        val existingAssets = release.assets.associateBy { it.name }
        logger.debug { "release has ${existingAssets.size} assets already" }

        for (artifact in artifacts) {
            logger.trace { "checking ${artifact.name} against existing assets" }

            val existing = existingAssets.getOrElse(artifact.name) {
                logger.debug { "${artifact.name} is not uploaded yet" }
                add(artifact)
                logger.trace { "${artifact.name} will be uploaded" }
                continue
            }

            // Already uploaded, replace if conflicting
            if (artifact conflictsWith existing) {
                logger.warn { "deleting conflicting asset ${existing.name}" }
                logger.debug { artifact.diff(existing, artifactName = "local", assetName = "existing") }

                client.deleteAsset(existing)

                logger.trace { "deleted ${existing.name}" }
                add(artifact)
                logger.trace { "${artifact.name} will be uploaded" }
            } else {
                logger.debug { "${existing.name} is already uploaded" }
                logger.trace { "${artifact.name} will NOT be uploaded" }
            }
        }
    }

    internal suspend fun uploadAsset(
        release: GitHubRelease,
        artifact: ReleaseArtifact,
    ) {
        logger.info { "publishing artifact ${artifact.name}" }
        logger.debug { "${artifact.name} is ${artifact.size} bytes" }

        val uploaded = client.uploadAssetToRelease(
            release = release,
            name = artifact.name,
            contentType = ContentType.Application.JavaArchive,
            content = artifact.artifact.readBytes(),
        )

        logger.debug { "uploaded ${uploaded.name}" }

        if (artifact conflictsWith uploaded) {
            logger.error { "artifact ${artifact.name} uploaded incorrectly" }
            logger.info { artifact.diff(uploaded, artifactName = "local", assetName = "uploaded") }
            error("${artifact.name} did not upload correctly")
        }
    }

    override fun close() = client.close()
}

private val ReleaseArtifact.name get() = artifact.name

private fun ReleaseArtifact.diff(
    asset: GitHubReleaseAsset,
    artifactName: String = "artifact",
    assetName: String = "asset",
) = """
    $artifactName size: $size
    $assetName size: ${asset.size}
    $artifactName SHA-256: ${sha256.toHexString()}
    $assetName SHA-256: ${asset.sha256?.toHexString() ?: "not available"}
""".trimIndent()

private infix fun ReleaseArtifact.conflictsWith(asset: GitHubReleaseAsset) =
    !contentMatches(asset)

private fun ReleaseArtifact.contentMatches(asset: GitHubReleaseAsset): Boolean =
    sequenceOf(
        asset.size == size,
        asset.sha256?.let(sha256::contentEquals),
    ).filterNotNull().all { it }
