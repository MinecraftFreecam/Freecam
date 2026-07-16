package net.xolt.freecam.config.load

import io.kotest.matchers.shouldBe
import net.xolt.freecam.util.fromJson
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.BeforeTest
import kotlin.test.Test

private data class TestConfig(
    var settingA: String = "defaultA",
    var settingB: Int = 42,
)

class BasicConfigLoaderTest {

    private lateinit var tempDir: Path
    private lateinit var configFile: Path
    private lateinit var serializer: GsonSerializer
    private lateinit var loader: ConfigLoader<TestConfig>

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory("config-test").apply {
            toFile().deleteOnExit()
        }
        configFile = tempDir.resolve("config.json")
        serializer = GsonSerializer()
        loader = BasicConfigLoader(serializer, TestConfig::class.java, configFile)
    }

    @Test
    fun `read returns fresh instance with defaults when no file exists`() {
        loader.read() shouldBe TestConfig()
    }

    @Test
    fun `loads json file`() {
        val configFile = tempDir.resolve("config.json")
        configFile.writeText("""{"settingA": "loadedA", "settingB": 100}""")

        val config = loader.read()

        config.settingA shouldBe "loadedA"
        config.settingB shouldBe 100
    }

    @Test
    fun `writes json file`() {
        val configFile = tempDir.resolve("config.json")
        configFile.writeText("""{"settingA": "oldA"}""")

        val loadedConfig = loader.read()

        loadedConfig.settingA = "newA"
        loader.write(loadedConfig)

        val result = configFile.fromJson<Map<*, *>>()

        result shouldBe mapOf(
            "settingA" to "newA",
            "settingB" to 42,
        )
    }
}
