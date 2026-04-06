package net.xolt.freecam.i18n

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import java.io.File
import java.nio.file.Files
import kotlin.test.Test

class LangDirConverterTest {

    @Test
    fun `directory is converted to json file`() = runTest {
        val tmp = Files.createTempDirectory("i18n").toFile().apply { deleteOnExit() }
        val dir = tmp.resolve("en_US").apply { mkdirs() }

        dir.resolve("ab.toml").writeText("""
            [a]
            b = "value"
        """.trimIndent())

        dir.resolve("xy.toml").writeText("""
            [x.y]
            z = "merged"
        """.trimIndent())

        val output = tmp.resolve("out.json")

        val converter = LangDirConverter(
            directory = dir,
            transformations = emptyList(),
            destination = output
        )

        converter.toLangJsonFile()

        output.exists() shouldBe true

        val values = withClue("Should be valid JSON") {
            Json.decodeFromString<Map<String, String>>(output.readText())
        }

        values shouldBe mapOf(
            "a.b" to "value",
            "x.y.z" to "merged",
        )
    }

    @Test
    fun `empty directory does not produce output`() = runTest {
        val tmp = Files.createTempDirectory("i18n").toFile().apply { deleteOnExit() }
        val dir = tmp.resolve("fr_FR").apply { mkdirs() }
        val output = tmp.resolve("out.json")

        LangDirConverter(
            directory = dir,
            transformations = emptyList(),
            destination = output,
        ).toLangJsonFile()

        output.exists() shouldBe false
    }

    @Test
    fun `flatten error includes full key path`() {
        val file = File.createTempFile("test", ".toml").apply {
            writeText("""
            [a]
            b = [1, 2, 3]
        """.trimIndent())
        }

        val converter = LangDirConverter(
            directory = file.parentFile,
            transformations = emptyList(),
            destination = File("out.json")
        )

        val ex = shouldThrow<IllegalStateException> {
            withErrorContext({ "[en_US] while flattening ${file.name}" }) {
                converter.run {
                    file.decodeFlatToml()
                }
            }
        }

        ex.message shouldContain "a.b"
    }

    @Test
    fun `flatten converts nested TOML to dot keys`() {
        val file = File.createTempFile("test", ".toml").apply {
            writeText("""
            [a]
            b = "value"
            
            [a.c]
            d = "nested"
        """.trimIndent())
        }

        val converter = LangDirConverter(
            directory = file.parentFile,
            transformations = emptyList(),
            destination = File("out.json")
        )

        val result = converter.run {
            file.decodeFlatToml()
        }

        result shouldBe mapOf(
            "a.b" to "value",
            "a.c.d" to "nested"
        )
    }

    @Test
    fun `renames for same key merge rename targets`() {
        val t1 = KeyTransformation("key", true, listOf("a"))
        val t2 = KeyTransformation("key", true, listOf("b"))

        val debugs = mutableListOf<String>()
        val logger = mockk<Logger> {
            every { debug(capture(debugs)) } returns Unit
        }

        val result = LangDirConverter(
            directory = File("foo"),
            transformations = listOf(t1, t2),
            destination = File("out.json"),
            logger = logger,
        ).run {
            mapOf("key" to "value").applyTransformations()
        }

        result shouldBe mapOf(
            "key" to "value",
            "a" to "value",
            "b" to "value",
        )
        debugs shouldBe listOf("[foo] renaming key 'key' to: 'a', 'b'")
    }

    @Test
    fun `keepOriginal true retains original key`() {
        val t = KeyTransformation("key", keepOriginal = true, names = listOf("a"))

        val debugs = mutableListOf<String>()
        val logger = mockk<Logger> {
            every { debug(capture(debugs)) } returns Unit
        }

        val result = LangDirConverter(
            directory = File("es_ES"),
            transformations = listOf(t),
            destination = File("out.json"),
            logger = logger,
        ).run {
            mapOf("key" to "value").applyTransformations()
        }

        result shouldBe mapOf(
            "key" to "value",
            "a" to "value",
        )
        debugs shouldBe listOf("[es_ES] renaming key 'key' to: 'a'")
    }

    @Test
    fun `keepOriginal false removes original key`() {
        val t = KeyTransformation("key", keepOriginal = false, names = listOf("a"))

        val debugs = mutableListOf<String>()
        val logger = mockk<Logger> {
            every { debug(capture(debugs)) } returns Unit
        }

        val converter = LangDirConverter(
            directory = File("locale"),
            transformations = listOf(t),
            destination = File("out.json"),
            logger = logger,
        )

        val result = converter.run {
            mapOf("key" to "value").applyTransformations()
        }

        result shouldBe mapOf("a" to "value")
        debugs shouldBe listOf(
            "[locale] renaming key 'key' to: 'a'",
            "[locale] removing original key 'key'",
        )
    }

    @Test
    fun `conflicting keepOriginal for same key throws`() {
        val t1 = KeyTransformation("key", keepOriginal = true, names = listOf("a"))
        val t2 = KeyTransformation("key", keepOriginal = false, names = listOf("b"))

        val logger = mockk<Logger>()
        val converter = LangDirConverter(
            directory = File("."),
            transformations = listOf(t1, t2),
            destination = File("out.json"),
            logger = logger,
        )

        val ex = shouldThrow<IllegalStateException> {
            converter.run {
                mapOf("key" to "value").applyTransformations()
            }
        }

        ex.message shouldContain "'key' have conflicting 'keepOriginal' values"
        ex.message shouldContain "key(names=['a'], keepOriginal=true)"
        ex.message shouldContain "key(names=['b'], keepOriginal=false)"
    }

    @Test
    fun `rename targets must be unique across input and transformations`() {
        val cases = listOf(
            // Collides with existing input key
            Triple(
                mapOf("key" to "value", "existing" to "other"),
                listOf(KeyTransformation("key", true, listOf("existing"))),
                "'existing'"
            ),
            // Collides with another transformation
            Triple(
                mapOf("k1" to "v1", "k2" to "v2"),
                listOf(
                    KeyTransformation("k1", true, listOf("dup")),
                    KeyTransformation("k2", true, listOf("dup")),
                ),
                "'dup'"
            )
        )

        cases.forEach { (input, transformations, expectedKey) ->
            val converter = LangDirConverter(
                directory = File("."),
                transformations = transformations,
                destination = File("out.json"),
                logger = mockk(relaxed = true),
            )

            val ex = shouldThrow<IllegalStateException> {
                converter.run { input.applyTransformations() }
            }

            ex.message shouldContain "specify renames that already exist"
            ex.message shouldContain expectedKey
        }
    }

    @Test
    fun `transformation missing key logs warning`() {
        val input = mapOf("existing.key" to "value")

        val warnings = mutableListOf<String>()
        val logger = mockk<Logger> {
            every { warn(capture(warnings)) } returns Unit
        }

        val transformation = KeyTransformation(
            name = "missing.key",
            keepOriginal = false,
            names = listOf("new.key")
        )

        val converter = LangDirConverter(
            directory = File("en_US"),
            transformations = listOf(transformation),
            destination = File("out.json"),
            logger = logger
        )

        val result = converter.run {
            input.applyTransformations()
        }

        withClue("Result should be a copy") {
            result shouldNotBeSameInstanceAs input
        }

        withClue("Result should be unchanged") {
            result shouldBe input
        }

        withClue("One warning should have been logged") {
            warnings.shouldHaveSize(1)
        }

        withClue("Warning should mention missing key") {
            warnings.single() shouldContain "Missing key 'missing.key'"
        }

        withClue("Warning should start with locale") {
            warnings.single() shouldStartWith "[en_US] "
        }
    }

    @Test
    fun `invalid TOML throws with context`() = runTest {
        val tmp = Files.createTempDirectory("i18n").toFile()
        val file = tmp.resolve("bad.toml").apply { writeText("this = [unterminated") }

        val converter = LangDirConverter(tmp, emptyList(), File(tmp, "out.json"))

        val ex = shouldThrow<IllegalStateException> {
            converter.toLangJsonFile()
        }

        ex.message shouldContain "while flattening"
    }
}