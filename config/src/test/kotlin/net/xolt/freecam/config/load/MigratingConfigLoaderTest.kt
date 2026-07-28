package net.xolt.freecam.config.load

import io.kotest.matchers.shouldBe
import io.mockk.*
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createTempDirectory
import kotlin.test.BeforeTest
import kotlin.test.Test

class MigratingConfigLoaderTest {

    private lateinit var tempDir: Path

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory("migrating-test").apply {
            toFile().deleteOnExit()
        }
    }

    @Test
    fun `read uses primary if neither exists`() {
        val primaryFile = tempDir.resolve("missing.json")
        val legacyFile = tempDir.resolve("legacy.json")

        val primaryLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns primaryFile
            every { read() } returns "primary_data"
        }
        val legacyLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns legacyFile
        }
        val loader = MigratingConfigLoader(primaryLoader, legacyLoader)

        val result = loader.read()

        result shouldBe "primary_data"
        verify(exactly = 1) { primaryLoader.read() }
        verify(exactly = 0) { legacyLoader.read() }
    }

    @Test
    fun `read uses primary if it exists`() {
        val primaryFile = tempDir.resolve("missing.json").createFile()
        val legacyFile = tempDir.resolve("legacy.json")

        val primaryLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns primaryFile
            every { read() } returns "primary_data"
        }
        val legacyLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns legacyFile
        }
        val loader = MigratingConfigLoader(primaryLoader, legacyLoader)

        val result = loader.read()

        result shouldBe "primary_data"
        verify(exactly = 1) { primaryLoader.read() }
        verify(exactly = 0) { legacyLoader.read() }
    }

    @Test
    fun `read falls back to legacy if primary is missing and legacy exists`() {
        val primaryFile = tempDir.resolve("missing.json")
        val legacyFile = tempDir.resolve("legacy.json").createFile()

        val primaryLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns primaryFile
        }
        val legacyLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns legacyFile
            every { read() } returns "legacy_data"
        }
        val loader = MigratingConfigLoader(primaryLoader, legacyLoader)

        val result = loader.read()

        result shouldBe "legacy_data"
        verify(exactly = 0) { primaryLoader.read() }
        verify(exactly = 1) { legacyLoader.read() }
    }

    @Test
    fun `read uses primary if both exist`() {
        val primaryFile = tempDir.resolve("missing.json").createFile()
        val legacyFile = tempDir.resolve("legacy.json").createFile()

        val primaryLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns primaryFile
            every { read() } returns "primary_data"
        }
        val legacyLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns legacyFile
        }
        val loader = MigratingConfigLoader(primaryLoader, legacyLoader)

        val result = loader.read()

        result shouldBe "primary_data"
        verify(exactly = 1) { primaryLoader.read() }
        verify(exactly = 0) { legacyLoader.read() }
    }

    @Test
    fun `write uses primary loader`() {
        val primaryFile = tempDir.resolve("missing.json").createFile()
        val legacyFile = tempDir.resolve("legacy.json").createFile()

        val primaryLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns primaryFile
            every { write(any()) } just Runs
        }
        val legacyLoader = mockk<ConfigLoader<String>> {
            every { filepath } returns legacyFile
        }
        val loader = MigratingConfigLoader(primaryLoader, legacyLoader)

        loader.write("written_data")

        verify(exactly = 1) { primaryLoader.write("written_data") }
        verify(exactly = 0) { legacyLoader.write(any()) }
    }
}
