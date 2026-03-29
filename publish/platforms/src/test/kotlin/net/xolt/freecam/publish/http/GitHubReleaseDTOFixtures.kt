package net.xolt.freecam.publish.http

import io.ktor.http.*

object GitHubReleaseDTOFixtures {

    fun testRelease(
        id: Long = 1234,
        url: String = "https://api.example.com",
        htmlUrl: String = "https://example.com",
        assetsUrl: String = "https://api.example.com/assets",
        uploadUrl: String = "https://upload.example.com{?name,label}",
        tagName: String = "v1.2.3",
        draft: Boolean = false,
        prerelease: Boolean = false,
        assets: List<GitHubReleaseAsset> = emptyList(),
    ) = GitHubRelease(
        id = id,
        url = Url(url),
        htmlUrl = Url(htmlUrl),
        assetsUrl = Url(assetsUrl),
        uploadUrlTemplate = uploadUrl,
        tagName = tagName,
        draft = draft,
        prerelease = prerelease,
        assets = assets,
    )

    fun testAsset(
        id: Long = 1234,
        url: String = "https://api.example.com/123",
        name: String = "my-asset",
        label: String? = null,
        contentType: String = ContentType.Application.JavaArchive.toString(),
        size: Long = 999,
        sha: ByteArray? = null,
        downloadCount: Long = 10,
        createdAt: String = "1970-01-01",
        updatedAt: String = "1970-01-01",
        downloadUrl: String = "https://api.example.com/123/download",
    ) = GitHubReleaseAsset(
        id = id,
        url = Url(url),
        name = name,
        label = label,
        contentType = contentType,
        size = size,
        digest = sha?.let { "sha256:${it.toHexString()}" },
        downloadCount = downloadCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        downloadUrl = Url(downloadUrl),
    )

}