package net.xolt.freecam.model

import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.xolt.freecam.serialization.lowerCaseEnumSerializer

@Serializable
data class ReleaseMetadata(
    @SerialName("mod_version") val modVersion: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("release_type") val releaseType: ReleaseType,
    val changelog: String,
    val platforms: Platforms,
    val versions: List<ProjectReleaseMetadata>,
)

@Serializable
data class ProjectReleaseMetadata(
    val loader: String,
    @SerialName("minecraft_version") val minecraft: String,
    val filename: String,
    @SerialName("game_versions") val gameVersions: List<String>,
    @SerialName("java_versions") val javaVersions: List<String>,
    val relationships: List<Relationship>,
) : Comparable<ProjectReleaseMetadata> {

    internal val minecraftSemver: Version by lazy {
        minecraft.toVersion(strict = false)
    }

    override fun compareTo(other: ProjectReleaseMetadata) = compareValuesBy(
        a = this, b = other,
        { it.minecraftSemver },
        { it.loader },
        { it.filename },
    )
}

@Serializable
data class Relationship(
    @SerialName("curseforge_slug") val curseforgeSlug: String,
    @SerialName("modrinth_id") val modrinthId: String,
    val type: Type
) : java.io.Serializable {
    @Serializable(with = Type.Serializer::class)
    enum class Type {
        REQUIRED, OPTIONAL, BUNDLED;

        object Serializer : KSerializer<Type> by lowerCaseEnumSerializer<Type>()
    }
}

@Serializable
data class Platforms(
    val curseforge: Curseforge,
    val modrinth: Modrinth,
    val github: Github
) {
    @Serializable
    data class Curseforge(
        @SerialName("project_id") val id: String
    )

    @Serializable
    data class Modrinth(
        @SerialName("project_id") val id: String
    )

    @Serializable
    data class Github(
        val tag: String
    )

}
