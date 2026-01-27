package net.xolt.freecam.gradle

import kotlinx.serialization.Serializable

@Serializable
data class MCVersionManifest(
    val versions: List<VersionEntry>
) {
    @Serializable
    data class VersionEntry(
        val id: String,
        val url: String
    )
}

