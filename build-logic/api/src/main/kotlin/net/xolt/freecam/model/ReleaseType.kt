package net.xolt.freecam.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ReleaseType {
    @SerialName("release") RELEASE,
    @SerialName("beta") BETA,
    @SerialName("alpha") ALPHA
}