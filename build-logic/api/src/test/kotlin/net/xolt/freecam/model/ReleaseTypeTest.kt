package net.xolt.freecam.model

import io.github.z4kn4fein.semver.Version
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ReleaseTypeTest {

    @Test
    fun `String toReleaseType`() {
        listOf(
            // "release" is a valid ReleaseType serial name,
            // even though it's not a valid pre-release label
            "release" to ReleaseType.RELEASE,
            "rc" to ReleaseType.RELEASE_CANDIDATE,
            "beta" to ReleaseType.BETA,
            "alpha" to ReleaseType.ALPHA,
        ).forEach { (input, expected) ->
            val result = input.toReleaseType()
            result shouldBe expected
        }
    }

    @Test
    fun `Version toReleaseType`() {
        listOf(
            Version() to ReleaseType.RELEASE,
            Version(preRelease = "rc") to ReleaseType.RELEASE_CANDIDATE,
            Version(preRelease = "beta") to ReleaseType.BETA,
            Version(preRelease = "alpha") to ReleaseType.ALPHA,
            Version(preRelease = "rc.1")   to ReleaseType.RELEASE_CANDIDATE,
            Version(preRelease = "beta.2") to ReleaseType.BETA,
            Version(preRelease = "alpha.1") to ReleaseType.ALPHA,
        ).forEach { (input, expected) ->
            val result = input.toReleaseType()
            result shouldBe expected
        }
    }

    @Test
    fun `String toReleaseType - invalid string throws`() {
        listOf(
            "",
            "stable",
            "pre",
            "snapshot",
            "1.0",
            "rc.1",
            "beta.2",
            "alpha.1",
        ).forEach { input ->
            shouldThrow<IllegalArgumentException> {
                input.toReleaseType()
            }
        }
    }

    @Test
    fun `Version toReleaseType - unrecognised pre-release label throws`() {
        listOf("release", "snapshot", "nightly", "pre", "stable").forEach { label ->
            shouldThrow<IllegalArgumentException> {
                Version(preRelease = label).toReleaseType()
            }
        }
    }

    @Test
    fun `String and Version toReleaseType agree`() {
        listOf("rc", "beta", "alpha").forEach { label ->
            label.toReleaseType() shouldBe Version(preRelease = label).toReleaseType()
        }
    }
}
