package net.xolt.freecam.publish.http

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GitHubReleaseRequest(
    @SerialName("tag_name")
    val tagName: String? = null,
    @SerialName("target_commitish")
    val targetCommitish: String? = null,
    val name: String? = null,
    val body: String? = null,
    val prerelease: Boolean? = null,
    val draft: Boolean? = null,
)

@Serializable
data class GitHubRelease(
    val id: Long,
    val url: Url,
    @SerialName("html_url")
    val htmlUrl: Url,
    @SerialName("assets_url")
    val assetsUrl: Url,
    @SerialName("upload_url")
    val uploadUrlTemplate: String,
    @SerialName("tag_name")
    val tagName: String,
    val draft: Boolean,
    val prerelease: Boolean,
    val assets: List<GitHubReleaseAsset>,
) {
    @Transient
    val uploadUrl =
        // Strip RFC 6570 template from upload URL
        Url(uploadUrlTemplate.substringBefore('{'))
}

@Serializable
data class GitHubReleaseAsset(
    val id: Long,
    val url: Url,
    val name: String,
    val label: String?,
    @SerialName("content_type")
    val contentType: String,
    val size: Long,
    val digest: String?,
    @SerialName("download_count")
    val downloadCount: Long,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("browser_download_url")
    val downloadUrl: Url,
) {
    val sha256: ByteArray? by lazy {
        if (digest == null) return@lazy null
        val hex = digest.removePrefix("sha256:").lowercase()
        require(hex.length == 64) { "Invalid SHA-256 digest: $digest" }
        hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}
