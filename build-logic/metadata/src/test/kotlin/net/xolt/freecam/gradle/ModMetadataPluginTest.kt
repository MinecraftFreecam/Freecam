package net.xolt.freecam.gradle

import io.github.z4kn4fein.semver.Version
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.logging.Logger
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.test.Test

class ModMetadataPluginTest {

    private val plugin = ModMetadataPlugin()

    @Test
    fun `addSnapshotSuffix formats stable version cleanly with a healthy git state`() {
        val repository = mockk<GitRepository> {
            every { resolveMetadata() } returns success(GitMetadata("b4df00d", isDirty = false))
        }

        val baseVersion = Version.parse("1.2.0")
        val result = with(plugin) { baseVersion.addSnapshotSuffix(repository) }

        result.preRelease shouldBe "SNAPSHOT-b4df00d"
        result.toString() shouldBe "1.2.0-SNAPSHOT-b4df00d"
    }

    @Test
    fun `addSnapshotSuffix preserves internal pre-releases and tracks dirty status flags`() {
        val repository = mockk<GitRepository> {
            every { resolveMetadata() } returns success(GitMetadata("a1b2c3d", isDirty = true))
        }

        val baseVersion = Version.parse("2.0.0-rc.3")
        val result = with(plugin) { baseVersion.addSnapshotSuffix(repository) }

        result.preRelease shouldBe "rc.3-SNAPSHOT-a1b2c3d-dirty"
        result.toString() shouldBe "2.0.0-rc.3-SNAPSHOT-a1b2c3d-dirty"
    }

    @Test
    fun `addSnapshotSuffix drops existing SNAPSHOT tags to avoid duplicates`() {
        val repository = mockk<GitRepository> {
            every { resolveMetadata() } returns success(GitMetadata("cafeb0b", isDirty = false))
        }

        val baseVersion = Version.parse("1.0.0-SNAPSHOT")
        val result = with(plugin) { baseVersion.addSnapshotSuffix(repository) }

        result.preRelease shouldBe "SNAPSHOT-cafeb0b"
        result.toString() shouldBe "1.0.0-SNAPSHOT-cafeb0b"
    }

    @Test
    fun `addSnapshotSuffix logs error and gracefully falls back to plain SNAPSHOT`() {
        val repository = mockk<GitRepository> {
            every { resolveMetadata() } returns failure(IllegalStateException("Could not execute git"))
        }
        val logger = mockk<Logger> {
            every { error(any()) } just Runs
        }
        val plugin = ModMetadataPlugin(logger = logger)

        val baseVersion = Version.parse("1.5.1-beta.2")
        val result = with(plugin) { baseVersion.addSnapshotSuffix(repository) }

        result.preRelease shouldBe "beta.2-SNAPSHOT"
        result.toString() shouldBe "1.5.1-beta.2-SNAPSHOT"

        verify {
            logger.error(match { it.startsWith("Could not resolve git metadata: ") })
        }
    }
}
