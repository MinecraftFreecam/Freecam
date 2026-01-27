package net.xolt.freecam.gradle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MCVersionJson(
    val downloads: Downloads
) {
    @Serializable
    data class Downloads(
        @SerialName("client_mappings")
        val clientMappings: Download
    )

    @Serializable
    data class Download(
        val url: String
    )
}