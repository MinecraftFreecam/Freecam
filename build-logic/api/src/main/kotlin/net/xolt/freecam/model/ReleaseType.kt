package net.xolt.freecam.model

import io.github.z4kn4fein.semver.Version
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
enum class ReleaseType {
    @SerialName("release") RELEASE,
    @SerialName("rc") RELEASE_CANDIDATE,
    @SerialName("beta") BETA,
    @SerialName("alpha") ALPHA
}

/** Extract a [ReleaseType] from a semantic version string. */
fun ReleaseType.Companion.fromVersion(version: String, strict: Boolean = false): ReleaseType =
    Version.parse(version, strict).toReleaseType()

/** Extract a [ReleaseType] from a [Version]. */
internal fun Version.toReleaseType(): ReleaseType =
    preRelease
        ?.substringBefore('.')
        ?.toReleaseType()
        ?.also {
            require(it != ReleaseType.RELEASE) {
                "Semver pre-release label '$preRelease' resolved to RELEASE: not a pre-release type"
            }
        }
        ?: ReleaseType.RELEASE

/** Parse a string into a ReleaseType. */
fun String.toReleaseType(): ReleaseType =
    try {
        Json.decodeFromJsonElement(Json.encodeToJsonElement(this))
    } catch (_: SerializationException) {
        throw IllegalArgumentException("Invalid ReleaseType: $this")
    }
