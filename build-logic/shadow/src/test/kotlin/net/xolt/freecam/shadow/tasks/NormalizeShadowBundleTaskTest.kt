package net.xolt.freecam.shadow.tasks

import dev.eav.tomlkt.TomlLiteral
import dev.eav.tomlkt.TomlTable
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import net.xolt.freecam.model.FmlModEntry
import net.xolt.freecam.model.FmlModsToml
import org.junit.jupiter.api.Test

class NormalizeShadowBundleTaskTest {

    @Test
    fun `namePrefix extracts mod name by dropping numeric semantic version suffixes`() {
        "cloth-config-5.3.63".namePrefix shouldBe "cloth-config"
        "cloth-config-forge-5.3.63".namePrefix shouldBe "cloth-config-forge"
        "jei-1.17.1-8.3.0.0".namePrefix shouldBe "jei"
        "foo-v2-1.11".namePrefix shouldBe "foo-v2"
        "".namePrefix shouldBe ""

        withClue("No version or segments") {
            "freecam".namePrefix shouldBe "freecam"
        }
    }

    @Test
    fun `normalizeModsToml removes legacy invalid root fields`() {
        val input = TomlTable(
            "modLoader" to TomlLiteral("javafml"),
            "authors" to TomlLiteral("shedaniel"),
            "displayURL" to TomlLiteral("example.com"),
            "logoFile" to TomlLiteral("icon.png"),
        )

        val result = input.normalizeModsToml()

        result.keys shouldBe setOf(
            "modLoader",
            "logoFile",
        )
    }

    @Test
    fun `normalizeModsToml reports removed keys`() {
        val removed = mutableListOf<Pair<String, String>>()

        TomlTable(
            "authors" to TomlLiteral("foobar"),
            "displayURL" to TomlLiteral("example.com"),
        ).normalizeModsToml { key, value ->
            removed += key to value.toString()
        }

        removed shouldBe listOf(
            "authors" to "foobar",
            "displayURL" to "example.com",
        )
    }

    @Test
    fun `prefixLogoFile renames matching logo paths`() {
        val input = FmlModsToml(
            modLoader = "javafml",
            loaderVersion = "40",
            license = "MIT",
            logoFile = "assets/icon.png",
            mods = listOf(
                FmlModEntry(
                    modId = "cloth",
                    version = "1",
                    displayName = "Cloth",
                    logoFile = "assets/icon.png",
                )
            )
        )

        val result = input.prefixLogoFile("cloth-config", "assets/icon.png")

        result.logoFile shouldBe "assets/cloth-config-icon.png"
        result.mods.shouldBeSingleton { mod ->
            mod.logoFile shouldBe "assets/cloth-config-icon.png"
        }
    }

    @Test
    fun `prefixLogoFile ignores non-matching logo paths`() {
        val input = FmlModsToml(
            modLoader = "javafml",
            loaderVersion = "40",
            license = "MIT",
            logoFile = "assets/logo.png",
            mods = listOf(
                FmlModEntry(
                    modId = "cloth",
                    version = "1",
                    displayName = "Cloth",
                    logoFile = "assets/logo.png",
                )
            )
        )

        val result = input.prefixLogoFile("cloth-config", "assets/icon.png")

        result.logoFile shouldBe "assets/logo.png"
        result.mods.shouldBeSingleton { mod ->
            mod.logoFile shouldBe "assets/logo.png"
        }
    }

    @Test
    fun `prefixLogoFiles renames matching logos`() {
        val input = FmlModsToml(
            modLoader = "javafml",
            loaderVersion = "[1,)",
            license = "MIT",
            mods = listOf(
                FmlModEntry(
                    modId = "a",
                    version = "1.0.0",
                    displayName = "A",
                    logoFile = "assets/icon.png",
                ),
                FmlModEntry(
                    modId = "b",
                    version = "1.0.0",
                    displayName = "B",
                    logoFile = "assets/logo.png",
                ),
                FmlModEntry(
                    modId = "c",
                    version = "1.0.0",
                    displayName = "C",
                    logoFile = "other-icon.png",
                ),
            )
        )

        val result = input.prefixLogoFiles(
            prefix = "pfx",
            logoPaths = listOf(
                "assets/icon.png",
                "other-icon.png",
            )
        )

        result shouldBe result.copy(
            mods = listOf(
                FmlModEntry(
                    modId = "a",
                    version = "1.0.0",
                    displayName = "A",
                    logoFile = "assets/pfx-icon.png",
                ),
                FmlModEntry(
                    modId = "b",
                    version = "1.0.0",
                    displayName = "B",
                    logoFile = "assets/logo.png",
                ),
                FmlModEntry(
                    modId = "c",
                    version = "1.0.0",
                    displayName = "C",
                    logoFile = "pfx-other-icon.png",
                ),
            )
        )
    }
}
