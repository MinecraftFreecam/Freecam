package net.xolt.freecam.model

import kotlinx.serialization.Serializable
import java.net.URI
import java.net.URL

@JvmInline
@Serializable
value class UrlString(val value: String) {
    init {
        require(runCatching(UrlString::toJavaUrl).isSuccess) {
            "Invalid URL string: $value"
        }
    }

    fun toJavaUri(): URI = URI(value)
    fun toJavaUrl(): URL = toJavaUri().toURL()
}
