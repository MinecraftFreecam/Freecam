package net.xolt.freecam.publish

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.xolt.freecam.model.ReleaseMetadata

object MetadataLoader {

    private val json = Json {
        ignoreUnknownKeys = false
        prettyPrint = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun load(): ReleaseMetadata =
        requireNotNull(javaClass.getResourceAsStream("/release-metadata.json")) {
            "release-metadata.json not found on classpath"
        }.use {
            json.decodeFromStream<ReleaseMetadata>(it)
        }
}

fun loadMetadata() = MetadataLoader.load()
