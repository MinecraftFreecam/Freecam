package net.xolt.freecam.config.load

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.kotest.matchers.shouldBe
import net.xolt.freecam.util.fromJson
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.BeforeTest
import kotlin.test.Test

class ModConfigPipelineTest {

    private data class IntegrationConfig(
        var primary: String = "default"
    ) : RawJsonHolder {
        @Transient private var rawJson: JsonObject? = null
        override fun setRawJson(rawJson: JsonObject?) { this.rawJson = rawJson }
        override fun getRawJson(): JsonObject? = rawJson
    }

    private lateinit var tempDir: Path
    private lateinit var loader: BasicConfigLoader<IntegrationConfig, JsonElement>

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory("freecam-config-test").apply {
            toFile().deleteOnExit()
        }

        val serializer = RawJsonPreservingSerializer()
        loader = BasicConfigLoader(
            serializer,
            IntegrationConfig::class.java,
            tempDir.resolve("freecam.json")
        )
    }

    @Test
    fun `full pipeline reads, updates, and preserves unknown fields on write`() {
        // Simulate an older config file on disk
        val configFile = loader.filepath
        configFile.writeText("""{"primary": "loaded", "futureSetting": "preserved"}""")

        // Read through the pipeline
        val config = loader.read()
        config.primary shouldBe "loaded"

        // Update domain model
        config.primary = "updated"

        // Write back through the pipeline
        loader.write(config)

        // Verify physical file
        val result = configFile.fromJson<Map<*, *>>()

        result shouldBe mapOf(
            "primary" to "updated",
            "futureSetting" to "preserved",
        )
    }
}
