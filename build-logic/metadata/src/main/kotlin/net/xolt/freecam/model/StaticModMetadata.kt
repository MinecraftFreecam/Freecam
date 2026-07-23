package net.xolt.freecam.model

import dev.eav.tomlkt.Toml
import dev.eav.tomlkt.TomlNativeReader
import dev.eav.tomlkt.decodeFromReader
import io.github.z4kn4fein.semver.Version
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

internal fun File.loadStaticMetadata(): ModMetadataToml {
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
    @SerialName("version")
    override val releaseVersion: Version,
    @Transient
    override val version: Version = releaseVersion,
    @Transient
    override val buildDir: String = releaseVersion.toString(),
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
        releaseVersion.toReleaseType()
    }
}
