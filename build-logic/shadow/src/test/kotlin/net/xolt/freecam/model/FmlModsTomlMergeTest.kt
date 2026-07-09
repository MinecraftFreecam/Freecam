package net.xolt.freecam.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class FmlModsTomlMergeTest {

    private val baseToml = FmlModsToml(
        modLoader = "javafml",
        loaderVersion = "40",
        license = "MIT",
    )

    @Test
    fun `mergeWith takes top-level fields exclusively from base`() {
        val mods = listOf(FmlModEntry(modId = "freecam", version = "1.0", displayName = "Freecam"))
        val canonical = baseToml
        val incoming = FmlModsToml(
            modLoader = "lowcodefml",
            loaderVersion = "69",
            license = "GPL",
            mods = mods,
        )

        val result = canonical mergeWith incoming

        result shouldBe baseToml.copy(mods = mods)
    }

    @Test
    fun `mergeWith appends arrays in correct sequential order`() {
        val canonical = baseToml.copy(
            mods = listOf(FmlModEntry(modId = "freecam", version = "1.0", displayName = "Freecam")),
        )
        val incoming = baseToml.copy(
            mods = listOf(FmlModEntry(modId = "cloth-config", version = "2.0", displayName = "Cloth Config")),
        )

        val result = canonical.mergeWith(incoming)

        result.mods.map { it.modId } shouldBe listOf("freecam", "cloth-config")
    }

    @Test
    fun `mergeWith safely merges disjoint dependency maps without dropping keys`() {
        val canonical = baseToml.copy(
            dependencies = mapOf(
                "freecam" to listOf(FmlDependencyEntry(modId = "foo", versionRange = "40")),
            )
        )
        val incoming = baseToml.copy(
            dependencies = mapOf(
                "cloth-config" to listOf(FmlDependencyEntry(modId = "bar", versionRange = "1.0")),
            )
        )

        val result = canonical.mergeWith(incoming)

        val deps = result.dependencies
        deps?.keys shouldBe setOf("freecam", "cloth-config")
        deps?.get("freecam")?.map { it.modId } shouldBe listOf("foo")
        deps?.get("cloth-config")?.map { it.modId } shouldBe listOf("bar")
    }

    @Test
    fun `mergeWith concatenates dependency lists when maps share the same modId key`() {
        val canonical = baseToml.copy(
            dependencies = mapOf(
                "freecam" to listOf(FmlDependencyEntry(modId = "forge", versionRange = "40")),
            )
        )
        val incoming = baseToml.copy(
            dependencies = mapOf(
                "freecam" to listOf(FmlDependencyEntry(modId = "minecraft", versionRange = "1.20")),
            )
        )

        val result = canonical.mergeWith(incoming)

        val freecamDeps = result.dependencies?.get("freecam")
        freecamDeps?.map { it.modId } shouldBe listOf("forge", "minecraft")
    }
}
