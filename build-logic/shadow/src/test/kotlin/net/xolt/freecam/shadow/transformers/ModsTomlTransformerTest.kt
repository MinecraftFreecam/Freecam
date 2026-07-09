package net.xolt.freecam.shadow.transformers

import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import kotlin.test.Test

class ModsTomlTransformerTest {

    private fun createContext(content: String): TransformerContext {
        return TransformerContext(
            path = "META-INF/mods.toml",
            inputStream = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8)),
            relocators = emptySet(),
        )
    }

    @Test
    fun `first file processed is treated as canonical and retains root properties`() {
        val transformer = ModsTomlTransformer()
        val canonical = """
            modLoader = "javafml"
            loaderVersion = "[40,)"
            license = "MIT"
            [[mods]]
            modId = "freecam"
            version = "1.0.0"
            displayName = "Freecam"
        """.trimIndent()

        transformer.transform(createContext(canonical))

        val secondary = """
            modLoader = "wrongLoader"
            loaderVersion = "wrongVersion"
            license = "wrongLicense"
            [[mods]]
            modId = "cloth-config"
            version = "5.3.63"
            displayName = "Cloth Config"
        """.trimIndent()

        transformer.transform(createContext(secondary))

        val merged = transformer.mergedData

        merged.shouldNotBeNull()

        withClue("top-level primitives come from canonical TOML") {
            merged.modLoader shouldBe "javafml"
            merged.loaderVersion shouldBe "[40,)"
            merged.license shouldBe "MIT"
        }

        withClue("nested arrays are merged") {
            merged.mods.map { it.modId } shouldBe listOf("freecam", "cloth-config")
        }
    }
}
