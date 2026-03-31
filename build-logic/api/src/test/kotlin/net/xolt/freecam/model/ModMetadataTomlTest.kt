package net.xolt.freecam.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ModMetadataTomlTest {

    @Test
    fun `releaseType derived from stable version`() {
        val meta = metadata(version = "1.3.6")
        meta.releaseType shouldBe ReleaseType.RELEASE
    }

    @Test
    fun `releaseType derived from preRelease version`() {
        listOf(
            "2"   to ReleaseType.RELEASE,
            "2.0"   to ReleaseType.RELEASE,
            "2.0.0" to ReleaseType.RELEASE,
            "2.0.0-alpha" to ReleaseType.ALPHA,
            "2.0.0-beta" to ReleaseType.BETA,
            "2.0.0-rc"   to ReleaseType.RELEASE_CANDIDATE,
            "2.0.0-alpha.1" to ReleaseType.ALPHA,
            "2.0.0-beta.2" to ReleaseType.BETA,
            "2.0.0-rc.1"   to ReleaseType.RELEASE_CANDIDATE,
            "2.0-beta" to ReleaseType.BETA,
            "2.0-rc.1"   to ReleaseType.RELEASE_CANDIDATE,
        ).forEach { (version, expected) ->
            metadata(version = version).releaseType shouldBe expected
        }
    }

    private fun metadata(version: String) = ModMetadataToml(
        id = "",
        name = "",
        group = "",
        version = version,
        authors = emptyList(),
        description = "",
        license = "",
        homepageUrl = url(),
        sourceUrl = url(),
        issuesUrl = url(),
        githubReleasesUrl = url(),
        curseforgeUrl = url(),
        curseforgeId = "",
        modrinthUrl = url(),
        modrinthId = "",
        crowdinUrl = url(),
    )

    private fun url(url: String = "https://example.com") = UrlString(url)
}
