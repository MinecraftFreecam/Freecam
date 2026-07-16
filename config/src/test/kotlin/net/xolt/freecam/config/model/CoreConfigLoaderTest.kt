package net.xolt.freecam.config.model

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.BeforeTest
import kotlin.test.Test

private class DummyConfig : RawConfigHolder {
    var id: String = "dummy"
    private var rawConfig: RawConfigNode? = null

    override fun setRawConfig(rawJson: RawConfigNode?) {
        this.rawConfig = rawJson
    }

    override fun getRawConfig(): RawConfigNode? = rawConfig
}

class CoreConfigLoaderTest {

    lateinit var tempDir: Path

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("config-test").apply {
            toFile().deleteOnExit()
        }
    }

    @Test
    fun `read returns fresh instance when no config files exist`() {
        val serializer = mockk<ConfigSerializer>()
        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")

        val result = loader.read()

        result.id shouldBe "dummy"
    }

    @Test
    fun `read loads from primary json file when it exists`() {
        // Create a fake config file
        val primaryFile = tempDir.resolve("test.json")
        Files.writeString(primaryFile, "{}")

        val mockRawConfig = mockk<RawConfigNode>(relaxed = true)
        val serializer = mockk<ConfigSerializer>()

        every { serializer.parse(any()) } returns mockRawConfig
        every { serializer.deserialize(mockRawConfig, DummyConfig::class.java) } returns DummyConfig().apply { id = "loaded_primary" }

        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")
        val result = loader.read()

        result.id shouldBe "loaded_primary"
        result.rawConfig shouldBe mockRawConfig // Ensure holder was populated
    }

    @Test
    fun `read falls back to migrating legacy json5 file if primary is missing`() {
        val legacyFile = tempDir.resolve("test.json5")
        Files.writeString(legacyFile, "{}")

        val mockRawConfig = mockk<RawConfigNode>(relaxed = true)
        val serializer = mockk<ConfigSerializer>()

        every { serializer.parse(any()) } returns mockRawConfig
        every { serializer.deserialize(mockRawConfig, DummyConfig::class.java) } returns DummyConfig().apply { id = "loaded_legacy" }

        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")
        val result = loader.read()

        result.id shouldBe "loaded_legacy"
    }

    @Test
    fun `write creates missing directories and saves serialized config`() {
        val serializer = mockk<ConfigSerializer>(relaxed = true)
        // Point loader to a sub-directory that doesn't exist yet
        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir.resolve("nested_dir"), "test")

        val config = DummyConfig()
        val mockCurrentRaw = mockk<RawConfigNode>(relaxed = true)
        every { serializer.serialize(config) } returns mockCurrentRaw

        shouldNotThrow<Exception> {
            loader.write(config)
        }

        // Verify the nested directory and file were physically created
        val expectedPath = tempDir.resolve("nested_dir/test.json")
        Files.exists(expectedPath) shouldBe true

        // Verify the serializer was asked to write to the file
        verify(exactly = 1) { serializer.write(mockCurrentRaw, any()) }
    }
}
