package net.xolt.freecam.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ReleaseType {
    @SerialName("release") RELEASE,
    @SerialName("rc") RELEASE_CANDIDATE,
    @SerialName("beta") BETA,
    @SerialName("alpha") ALPHA
}