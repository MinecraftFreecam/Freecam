package net.xolt.freecam.model

import dev.eav.tomlkt.Toml
import dev.eav.tomlkt.TomlNativeReader
import dev.eav.tomlkt.decodeFromReader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

internal fun File.loadStaticMetadata(): StaticModMetadata {
    val metadata: MetadataToml = bufferedReader().use { reader ->
        Toml.decodeFromReader(TomlNativeReader(reader))
    }
    return metadata.mod
}

@Serializable
internal data class MetadataToml(
    val mod: ModMetadataToml,
)

@Serializable
internal data class ModMetadataToml(
    override val id: String,
    override val name: String,
    override val group: String,
    override val version: String,
    override val authors: List<String>,
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
    override val curseforgeId: ULong,
    @SerialName("modrinth")
    override val modrinthUrl: UrlString,
    @SerialName("modrinth_id")
    override val modrinthId: String,
    @SerialName("crowdin")
    override val crowdinUrl: UrlString,
) : StaticModMetadata {

    override val releaseType: ReleaseType by lazy {
        ReleaseType.fromVersion(version)
    }
}
