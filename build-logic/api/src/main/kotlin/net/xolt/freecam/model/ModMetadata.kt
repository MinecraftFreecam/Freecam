package net.xolt.freecam.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModMetadata(
    val id: String,
    val name: String,
    val group: String,
    val version: String,
    @SerialName("release_type")
    val releaseType: ReleaseType,
    val authors: List<String>,
    val description: String,
    val license: String,
    @SerialName("homepage")
    val homepageUrl: UrlString,
    @SerialName("source")
    val sourceUrl: UrlString,
    @SerialName("issues")
    val issuesUrl: UrlString,
    @SerialName("github_releases")
    val githubReleasesUrl: UrlString,
    @SerialName("curseforge")
    val curseforgeUrl: UrlString,
    @SerialName("curseforge_id")
    val curseforgeId: String,
    @SerialName("modrinth")
    val modrinthUrl: UrlString,
    @SerialName("modrinth_id")
    val modrinthId: String,
    @SerialName("crowdin")
    val crowdinUrl: UrlString,
)
