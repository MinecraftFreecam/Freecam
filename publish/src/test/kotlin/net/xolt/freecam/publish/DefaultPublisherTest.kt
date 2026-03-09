package net.xolt.freecam.publish

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.mockk.MockKMatcherScope
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.publish.model.ReleaseArtifact
import net.xolt.freecam.publish.platforms.GitHubPlatform
import net.xolt.freecam.publish.platforms.ModrinthPlatform
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.createTestDir
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.pathString
import kotlin.test.Test

class DefaultPublisherTest {

    private val testVersions = listOf(
        testProjectReleaseMetadata(filename = "a"),
        testProjectReleaseMetadata(filename = "b"),
        testProjectReleaseMetadata(filename = "c"),
        testProjectReleaseMetadata(filename = "d"),
    )

    @Test
    fun `publisher calls each platform with all artifacts`() = runTest {
        val github = mockk<GitHubPlatform>(relaxUnitFun = true)
        val modrinth = mockk<ModrinthPlatform>(relaxUnitFun = true)

        val dir = createTestDir()
        val metadata = testMetadata(versions = testVersions)
        val metadataArtifacts = metadata.versions.map {
            dir.resolve(it.filename).apply(Path::createFile)
        }

        DefaultPublisher(dir, github, modrinth).publish(metadata)

        fun MockKMatcherScope.verifyArtifacts() = match<List<ReleaseArtifact>> { artifacts ->
            artifacts.map { it.artifact } == metadataArtifacts
        }

        coVerify { github.publishRelease(metadata, verifyArtifacts()) }
        coVerify { modrinth.publishRelease(metadata, verifyArtifacts()) }
    }

    @Test
    fun `fails if artifact missing`(): Unit = runTest {
        val github = mockk<GitHubPlatform>(relaxUnitFun = true)
        val modrinth = mockk<ModrinthPlatform>(relaxUnitFun = true)

        val dir = createTestDir()
        val metadata = testMetadata(versions = testVersions)
        val publisher = DefaultPublisher(dir, github, modrinth)

        val ex = shouldThrowExactly<IllegalArgumentException> {
            publisher.publish(metadata)
        }

        ex.message shouldContainOnlyOnce "artifacts were not found"
        assertSoftly {
            metadata.versions
                .map { dir.resolve(it.filename) }
                .forEach { ex.message shouldContainOnlyOnce  it.pathString }
        }
    }
}

private fun testProjectReleaseMetadata(
    loader: String = "fabric",
    minecraft: String = "26.1",
    filename: String = "file.jar",
    gameVersions: List<String> = emptyList(),
    javaVersions: List<String> = emptyList(),
    relationships: List<Relationship> = emptyList(),
) = ProjectReleaseMetadata(
    loader = loader,
    minecraft = minecraft,
    filename = filename,
    gameVersions = gameVersions,
    javaVersions = javaVersions,
    relationships = relationships,
)