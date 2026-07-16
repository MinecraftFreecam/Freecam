package net.xolt.freecam.config.model

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
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
        val serializer = mockk<ConfigSerializer<RawConfigNode>>()
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
        val serializer = mockk<ConfigSerializer<RawConfigNode>> {
            every { parse(any()) } returns mockRawConfig
            every { deserialize(mockRawConfig, DummyConfig::class.java) } returns DummyConfig().apply { id = "loaded_primary" }
        }

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
        val serializer = mockk<ConfigSerializer<RawConfigNode>> {
            every { parse(any()) } returns mockRawConfig
            every { deserialize(mockRawConfig, DummyConfig::class.java) } returns DummyConfig().apply { id = "loaded_legacy" }
        }

        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")
        val result = loader.read()

        result.id shouldBe "loaded_legacy"
    }

    @Test
    fun `write creates missing directories and saves serialized config`() {
        val serializer = mockk<ConfigSerializer<RawConfigNode>>(relaxed = true)
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

    @Test
    fun `write preserves unknown top-level keys`() {
        val current = mockk<RawConfigNode>(relaxed = true)
        val previous = mockk<RawConfigNode>(relaxed = true)
        val result = mockk<RawConfigNode>(relaxed = true)
        val unknown = mockk<RawConfigNode>(relaxed = true)
        val unknownCopy = mockk<RawConfigNode>(relaxed = true)
        val serializer = mockk<ConfigSerializer<RawConfigNode>> {
            every { serialize(any<DummyConfig>()) } returns current
            every { deepCopy(current) } returns result
            every { deepCopy(unknown) } returns unknownCopy
            every { write(result, any()) } returns Unit
            every { entries(previous) } returns listOf("unknown" to unknown).map { it.asEntry() }
            every { get(current, "unknown") } returns null
            every { add(any(), any(), any()) } just Runs
        }


        val config = DummyConfig().apply {
            rawConfig = previous
        }

        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")

        loader.write(config)

        verify {
            serializer.add(result, "unknown", unknownCopy)
        }
    }

    @Test
    fun `merge does not call isObject for missing current key`() {
        val current = mockk<RawConfigNode>(relaxed = true)
        val previous = mockk<RawConfigNode>(relaxed = true)
        val result = mockk<RawConfigNode>(relaxed = true)
        val previousValue = mockk<RawConfigNode>(relaxed = true)

        val serializer = mockk<ConfigSerializer<RawConfigNode>> {
            every { serialize(any<DummyConfig>()) } returns current
            every { deepCopy(current) } returns result
            every { deepCopy(previousValue) } returns previousValue
            every { write(any(), any()) } returns Unit

            every { entries(previous) } returns listOf("missing" to previousValue).map { it.asEntry() }
            every { get(current, "missing") } returns null
            every { add(any(), any(), any()) } just Runs
        }

        val config = DummyConfig().apply {
            rawConfig = previous
        }

        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")

        loader.write(config)

        verify(exactly = 0) {
            serializer.isObject(current)
        }

        verify(exactly = 0) {
            serializer.isObject(previousValue)
        }
    }

    @Test
    fun `write recursively merges nested objects`() {
        val current = mockk<RawConfigNode>(relaxed = true)
        val previous = mockk<RawConfigNode>(relaxed = true)
        val result = mockk<RawConfigNode>(relaxed = true)

        val currentChild = mockk<RawConfigNode>(relaxed = true)
        val previousChild = mockk<RawConfigNode>(relaxed = true)
        val mergedChild = mockk<RawConfigNode>(relaxed = true)

        val serializer = mockk<ConfigSerializer<RawConfigNode>> {
            every { serialize(any<DummyConfig>()) } returns current
            every { deepCopy(current) } returns result
            every { deepCopy(currentChild) } returns mergedChild
            every { write(any(), any()) } returns Unit

            every { entries(previous) } returns listOf("nested" to previousChild).map { it.asEntry() }

            every { get(current, "nested") } returns currentChild

            every { isObject(currentChild) } returns true
            every { isObject(previousChild) } returns true

            every { entries(currentChild) } returns emptyList()
            every { entries(previousChild) } returns emptyList()

            every { add(any(), any(), any() ) } just Runs
        }

        val config = DummyConfig().apply {
            rawConfig = previous
        }

        val loader = CoreConfigLoader(serializer, DummyConfig::class.java, tempDir, "test")

        loader.write(config)

        verify {
            serializer.add(result, "nested", any())
        }
    }
}

private fun <K, V> Pair<K, V>.asEntry() = object : Map.Entry<K, V> {
    override val key get() = first
    override val value get() = second
}
