package net.xolt.freecam.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.xolt.freecam.serialization.lowerCaseEnumSerializer
import org.gradle.api.JavaVersion

@Serializable
data class ReleaseMetadata(
    @SerialName("mod_version") val modVersion: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("release_type") val releaseType: String,
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
    @SerialName("java_versions") val javaVersions: List<JavaVersion>,
    val relationships: List<Relationship>,
)

@Serializable
data class Relationship(
    @SerialName("curseforge_slug") val curseforgeSlug: String,
    @SerialName("modrinth_id") val modrinthId: String,
    val type: Type
) {
    @Serializable(with = Type.Serializer::class)
    enum class Type {
        REQUIRED, OPTIONAL, BUNDLED;
        private object Serializer : KSerializer<Type> by lowerCaseEnumSerializer<Type>()
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
