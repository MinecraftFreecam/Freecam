package net.xolt.freecam.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface PropertyProvider {
    operator fun get(prop: String): String
    fun orNull(prop: String): String?
}

interface ModMetadata : StaticModMetadata {
    val mc: String
    val loader: String
    val properties: PropertyProvider
    val mod: PropertyProvider
    val deps: PropertyProvider
    val relationships: List<Relationship>

    fun parchment(block: (mappings: String, minecraft: String) -> Unit)
}

interface StaticModMetadata {
    val id: String
    val name: String
    val group: String
    val version: String
    val releaseType: ReleaseType
    val authors: List<String>
    val description: String
    val license: String
    val homepageUrl: UrlString
    val sourceUrl: UrlString
    val issuesUrl: UrlString
    val githubReleasesUrl: UrlString
    val curseforgeUrl: UrlString
    val curseforgeId: String
    val modrinthUrl: UrlString
    val modrinthId: String
    val crowdinUrl: UrlString
}

@Serializable
data class ModMetadataToml(
    override val id: String,
    override val name: String,
    override val group: String,
    override val version: String,
    @SerialName("release_type")
    override val releaseType: ReleaseType,
    override val authors: List<String>,
    override val description: String,
    override val license: String,
    @SerialName("homepage")
    override val homepageUrl: UrlString,
    @SerialName("source")
    override val sourceUrl: UrlString,
    @SerialName("issues")
    override val issuesUrl: UrlString,
    @SerialName("github_releases")
    override val githubReleasesUrl: UrlString,
    @SerialName("curseforge")
    override val curseforgeUrl: UrlString,
    @SerialName("curseforge_id")
    override val curseforgeId: String,
    @SerialName("modrinth")
    override val modrinthUrl: UrlString,
    @SerialName("modrinth_id")
    override val modrinthId: String,
    @SerialName("crowdin")
    override val crowdinUrl: UrlString,
) : StaticModMetadata