package net.xolt.freecam.publish.platforms

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.model.ReleaseType
import net.xolt.freecam.publish.http.GitHubRelease
import net.xolt.freecam.publish.http.GitHubReleaseAsset
import net.xolt.freecam.publish.http.GitHubReleaseDTOFixtures.testRelease
import net.xolt.freecam.publish.http.GitHubReleasesClient
import net.xolt.freecam.publish.logging.TestLogger
import net.xolt.freecam.publish.model.GitHubConfig
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.createTestFile
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultGitHubPlatformTest {

    private data object TestConfig : GitHubConfig {
        override val owner: String = "o"
        override val repo: String = "r"
        override val token: String = "t"
        override val headSha: String = "abc123"
    }

    private lateinit var logger: TestLogger
    private lateinit var client: GitHubReleasesClient
    private lateinit var platform: DefaultGitHubPlatform

    @BeforeTest
    fun setup() {
        logger = TestLogger()
        client = mockk<GitHubReleasesClient>()
        platform = DefaultGitHubPlatform(TestConfig, client, logger)
    }

    @Test
    fun `reconcileRelease creates release when none exists`() = runTest {
        val tag = "v1.0.0"
        val metadata = testMetadata(
            githubTag = tag,
        )
        val release = testRelease(
            tagName = tag,
        )

        coEvery { client.getReleaseIDByTag(tag) } returns null
        coEvery { client.createRelease(any(), any(), any(), any(), any(), any()) } returns release

        val result = platform.reconcileRelease(metadata)

        coVerify(exactly = 1) {
            client.createRelease(
                tagName = tag,
                targetCommitish = "abc123",
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = false,
                draft = true
            )
        }

        coVerify(exactly = 0) { client.updateRelease(any<Long>(), any(), any(), any(), any(), any()) }

        result shouldBeSameInstanceAs release
    }

    @Test
    fun `reconcileRelease updates release when one already exists`() = runTest {
        val tag = "v1.0.0"
        val metadata = testMetadata(githubTag = tag)
        val existing = 123L
        val updated = testRelease(tagName = tag, id = 12345)

        coEvery { client.getReleaseIDByTag(tag) } returns existing
        coEvery {
            client.updateRelease(
                releaseId = existing,
                tagName = tag,
                targetCommitish = "abc123",
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = false,
            )
        } returns updated

        val result = platform.reconcileRelease(metadata)

        coVerify(exactly = 1) {
            client.updateRelease(
                releaseId = existing,
                tagName = tag,
                targetCommitish = "abc123",
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = false,
            )
        }

        coVerify(exactly = 0) { client.createRelease(any(), any(), any(), any(), any(), any()) }

        result shouldBeSameInstanceAs updated
    }

    @Test
    fun `reconcileRelease marks release as prerelease when release type is not RELEASE`() = runTest {
        val tag = "v1.0.0-beta"
        val metadata = testMetadata(
            githubTag = tag,
            releaseType = ReleaseType.BETA,
        )

        coEvery { client.getReleaseIDByTag(tag) } returns null
        coEvery { client.createRelease(any(), any(), any(), any(), any(), any()) } returns testRelease()

        platform.reconcileRelease(metadata)

        coVerify {
            client.createRelease(
                tagName = tag,
                targetCommitish = "abc123",
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = true,
                draft = true,
            )
        }
    }

    @Test
    fun `reconcileRelease passes prerelease flag when updating`() = runTest {
        val id = 123L
        val tag = "v1.0.0-beta"
        val metadata = testMetadata(
            githubTag = tag,
            releaseType = ReleaseType.BETA,
        )
        val release = testRelease(
            id = id,
            tagName = tag,
            prerelease = true
        )

        coEvery { client.getReleaseIDByTag(tag) } returns id
        coEvery { client.updateRelease(id, any(), any(), any(), any(), any()) } returns release

        val result = platform.reconcileRelease(metadata)

        result shouldBeSameInstanceAs release

        coVerify {
            client.updateRelease(
                releaseId = id,
                tagName = tag,
                targetCommitish = "abc123",
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = true,
            )
        }
    }

    @Test
    fun `reconcileRelease respects explicit draft parameter`() = runTest {
        val tag = "v1.0.0"
        val metadata = testMetadata(githubTag = tag)

        coEvery { client.getReleaseIDByTag(tag) } returns null
        coEvery { client.createRelease(any(), any(), any(), any(), any(), any()) } returns testRelease()

        platform.reconcileRelease(metadata, draft = false)

        coVerify {
            client.createRelease(
                tagName = tag,
                targetCommitish = "abc123",
                name = metadata.displayName,
                body = metadata.changelog,
                prerelease = false,
                draft = false,
            )
        }
    }

    @Test
    fun `publishRelease undrafts release after upload`() = runTest {
        val id = 123L
        val release = testRelease(id = id, draft = true)

        coEvery { client.getReleaseIDByTag(any()) } returns id
        coEvery { client.updateRelease(id, any(), any(), any(), any(), any()) } returns release
        coEvery { client.updateDraftState(release, false) } returns release

        platform.publishRelease(testMetadata(), emptyList())

        coVerify(exactly = 1) { client.updateDraftState(release, false) }
    }

    @Test
    fun `publishRelease does not undraft when already published`() = runTest {
        val id = 123L
        val release = testRelease(id = id, draft = false)

        coEvery { client.getReleaseIDByTag(any()) } returns id
        coEvery { client.updateRelease(id, any(), any(), any(), any(), any()) } returns release

        platform.publishRelease(testMetadata(), emptyList())

        coVerify(exactly = 0) { client.updateDraftState(any<Long>(), any()) }
        coVerify(exactly = 0) { client.updateDraftState(any<GitHubRelease>(), any()) }
    }

    @Test
    fun `reconcileAssets returns empty when asset matches`() = runTest {
        val artifact = mockArtifact(sha = byteArrayOf(1, 2, 3))
        val existing = mockAsset(sha = byteArrayOf(1, 2, 3))

        val release = testRelease(assets = listOf(existing))

        val result = platform.reconcileAssets(release, listOf(artifact))

        result shouldBe emptyList()
        coVerify(exactly = 0) { client.deleteAsset(any()) }
    }

    @Test
    fun `reconcileAssets returns artifact when none exists`() = runTest {
        val artifact = mockArtifact(sha = byteArrayOf(1, 2, 3))

        val release = testRelease(assets = emptyList())

        val result = platform.reconcileAssets(release, listOf(artifact))

        result shouldContainExactly listOf(artifact)
        coVerify(exactly = 0) { client.deleteAsset(any()) }
    }

    @Test
    fun `reconcileAssets deletes and returns artifact when sha differs`() = runTest {
        val artifact = mockArtifact(sha = byteArrayOf(9, 9, 9))
        val existing = mockAsset(sha = byteArrayOf(1, 2, 3))
        val release = testRelease(assets = listOf(existing))

        coEvery { client.deleteAsset(existing) } just Runs

        val result = platform.reconcileAssets(release, listOf(artifact))

        result shouldContainExactly listOf(artifact)
        coVerify(exactly = 1) { client.deleteAsset(existing) }
    }

    @Test
    fun `reconcileAssets deletes when size differs and sha unavailable`() = runTest {
        val artifact = mockArtifact(sha = byteArrayOf(1, 2, 3), size = 23)
        val existing = mockAsset(sha = null, size = 25)
        val release = testRelease(assets = listOf(existing))

        coEvery { client.deleteAsset(existing) } just Runs

        val result = platform.reconcileAssets(release, listOf(artifact))

        result shouldContainExactly listOf(artifact)
        coVerify { client.deleteAsset(existing) }
    }

    @Test
    fun `reconcileAssets handles mixed matching and differing artifacts`() = runTest {
        val artifact1 = mockArtifact(name = "a.jar", sha = byteArrayOf(1))
        val artifact2 = mockArtifact(name = "b.jar", sha = byteArrayOf(9))

        val existing1 = mockAsset(name = "a.jar", sha = byteArrayOf(1))
        val existing2 = mockAsset(name = "b.jar", sha = byteArrayOf(2))

        val release = testRelease(assets = listOf(existing1, existing2))

        coEvery { client.deleteAsset(existing2) } just Runs

        val result = platform.reconcileAssets(release, listOf(artifact1, artifact2))

        result shouldContainExactly listOf(artifact2)
        coVerify(exactly = 1) { client.deleteAsset(existing2) }
    }

    @Test
    fun `publishRelease fails when uploaded asset does not match`() = runTest {
        val id = 123L
        val artifact = mockArtifact(sha = byteArrayOf(1, 2, 3))
        val asset = mockAsset(sha = byteArrayOf(9, 9, 9))
        val release = testRelease(draft = false)

        coEvery { client.getReleaseIDByTag(any()) } returns id
        coEvery { client.updateRelease(id, any(), any(), any(), any(), any()) } returns release
        coEvery { client.uploadAssetToRelease(any(), any(), any(), any(), any()) } returns asset

        val ex = shouldThrow<IllegalStateException> {
            platform.publishRelease(testMetadata(), listOf(artifact))
        }

        ex.message shouldBe "file.jar did not upload correctly"
    }
}

private fun mockArtifact(
    name: String = "file.jar",
    sha: ByteArray,
    size: Long = 23,
): ReleaseArtifact {
    val path = createTestFile(name = name)
    return mockk {
        every { artifact } returns path
        every { sha256 } returns sha
        every { this@mockk.size } returns size
    }
}

private fun mockAsset(
    name: String = "file.jar",
    sha: ByteArray? = null,
    size: Long = 23,
) = mockk<GitHubReleaseAsset> {
    every { this@mockk.name } returns name
    every { sha256 } returns sha
    every { this@mockk.size } returns size
}
