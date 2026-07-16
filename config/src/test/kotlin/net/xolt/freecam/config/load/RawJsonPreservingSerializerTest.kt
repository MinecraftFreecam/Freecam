package net.xolt.freecam.config.load

import com.google.gson.JsonObject
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import net.xolt.freecam.util.toJsonTree
import kotlin.test.Test

class RawJsonPreservingSerializerTest {

    private data class DummyConfig(
        var knownField: String = "default",
        @Transient private var rawJson: JsonObject? = null,
    ) : RawJsonHolder {
        override fun setRawJson(rawJson: JsonObject?) { this.rawJson = rawJson }
        override fun getRawJson(): JsonObject? = rawJson
    }

    private val serializer = RawJsonPreservingSerializer()

    @Test
    fun `deserialize injects raw json into holder`() {
        val json = mapOf(
            "knownField" to "loaded",
            "unknownField" to 42,
        ).toJsonTree()

        val config = serializer.deserialize(json, DummyConfig::class.java)

        config.shouldNotBeNull {
            knownField shouldBe "loaded"
            rawJson shouldBe json.asJsonObject
        }
    }

    @Test
    fun `serialize preserves unknown top-level keys`() {
        val config = DummyConfig(
            knownField = "updated",
            rawJson = mapOf(
                "knownField" to "old",
                "unknownField" to 99,
            ).toJsonTree().asJsonObject,
        )

        val result = serializer.serialize(config)

        result shouldBe mapOf(
            "knownField" to "updated",
            "unknownField" to 99,
        ).toJsonTree()
    }

    @Test
    fun `serialize deeply merges nested objects`() {
        val config = DummyConfig(knownField = "updated")
        val oldRawJson = mapOf(
            "nested" to mapOf(
                "keptOld" to true,
                "overwritten" to "old",
            ),
        ).toJsonTree().asJsonObject
        config.rawJson = oldRawJson

        val currentJson = mapOf(
              "knownField" to "updated",
              "nested" to mapOf(
                "overwritten" to "new",
                "brandNew" to 1,
              ),
        ).toJsonTree().asJsonObject

        val result = serializer.merge(currentJson, oldRawJson).asJsonObject

        result shouldBe mapOf(
            "knownField" to "updated",
            "nested" to mapOf(
                "keptOld" to true,
                "overwritten" to "new",
                "brandNew" to 1,
            ),
        ).toJsonTree().asJsonObject
    }
}
