package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.Publisher
import net.xolt.freecam.publish.PublisherFactory
import net.xolt.freecam.publish.model.GitHubConfig
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.createTestDir
import net.xolt.freecam.test.createTestFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class PublishCliCommandTest {

    @Test
    fun `commandName is publish`() {
        val cmd = testCommand()
        cmd.commandName shouldBe "publish"
    }

    @Test
    fun `--help prints usage and exits 0`() = runTest {
        val cmd = testCommand()
        val result = cmd.test("--help")

        result.statusCode shouldBe 0
        result.output shouldContain "Usage"
        result.output shouldContain "artifacts-dir"
        result.output shouldContain "--gh-token"
    }

    @Test
    fun `--version prints version and exits 0`() = runTest {
        val cmd = testCommand()
        val result = cmd.test("--version")

        result.output shouldContain "version ${testMetadata().modVersion}"
        result.statusCode shouldBe 0
    }

    @Test
    fun `dry-run uses dry publisher`() = runTest {
        val metadata = testMetadata()
        val dir = createTestDir()
        var dryPublisher: Boolean? = null
        var actualDir: Path? = null

        val publisher = mockk<Publisher>(relaxUnitFun = true)
        val publisherFactory = object : PublisherFactory {
            override fun create(
                dryRun: Boolean,
                artifactsDir: Path,
                githubConfig: GitHubConfig,
            ): Publisher {
                dryPublisher = dryRun
                actualDir = artifactsDir
                return publisher
            }
        }

        val cmd = testCommand(metadata = metadata, publisherFactory = publisherFactory)
        val result = cmd.test(listOf(
            "--dry-run",
            "--gh-token", "token",
            "--gh-owner", "owner",
            "--gh-repo", "repo",
            "--git-sha", "committish",
            dir.absolutePathString(),
        ))

        result.statusCode shouldBe 0
        cmd.dryRun shouldBe true
        dryPublisher shouldBe true
        actualDir shouldBe dir.absolute()
        result.statusCode shouldBe 0
        coVerifySequence {
            publisher.publish(metadata)
            publisher.close()
        }
        confirmVerified(publisher)
    }

    @Test
    fun `non-dry-run uses 'real' publisher`() = runTest {
        val metadata = testMetadata()
        var dryPublisher: Boolean? = null
        var actualDir: Path? = null

        val publisher = mockk<Publisher>(relaxUnitFun = true)
        val publisherFactory = object : PublisherFactory {
            override fun create(
                dryRun: Boolean,
                artifactsDir: Path,
                githubConfig: GitHubConfig,
            ): Publisher {
                dryPublisher = dryRun
                actualDir = artifactsDir
                return publisher
            }
        }

        // We need a real directory or validation will fail
        val dir = createTestDir()

        val cmd = testCommand(metadata = metadata, publisherFactory = publisherFactory)
        val result = cmd.test(listOf(
            "--gh-token", "token",
            "--gh-owner", "owner",
            "--gh-repo", "repo",
            "--git-sha", "committish",
            dir.absolutePathString(),
        ))

        result.statusCode shouldBe 0
        cmd.dryRun shouldBe false
        dryPublisher shouldBe false
        actualDir shouldBe dir.absolute()
        coVerifySequence {
            publisher.publish(metadata)
            publisher.close()
        }
        confirmVerified(publisher)
    }

    @Test
    fun `missing artifacts-dir fails validation`() = runTest {
        val metadata = testMetadata()

        val dir = Path("artifacts-dir")

        val cmd = testCommand(metadata = metadata)
        val result = cmd.test(listOf(
            "--gh-token", "token",
            "--gh-owner", "owner",
            "--gh-repo", "repo",
            "--git-sha", "committish",
            dir.absolutePathString(),
        ))

        result.statusCode shouldBe 1
        result.stderr shouldContain "Error: invalid value for <artifacts-dir>: ${dir.absolute()} does not exist"
    }

    @Test
    fun `non-dir artifacts-dir fails validation`() = runTest {
        val metadata = testMetadata()

        val file = createTestFile()
        val cmd = testCommand(metadata = metadata)

        val result = cmd.test(listOf(
            "--gh-token", "token",
            "--gh-owner", "owner",
            "--gh-repo", "repo",
            "--git-sha", "committish",
            file.absolutePathString(),
        ))

        result.statusCode shouldBe 1
        result.stderr shouldContain "Error: invalid value for <artifacts-dir>: ${file.absolute()} is not a directory"
    }
}

internal fun testCommand(
    metadata: ReleaseMetadata? = null,
    publisherFactory: PublisherFactory = mockPublisherFactory(),
): PublishCliCommand {
    return PublishCliCommand(metadataSupplier = { metadata ?: testMetadata() }, publisherFactory = publisherFactory)
}

internal fun mockPublisherFactory(
    relaxUnitFun: Boolean = false,
) = object : PublisherFactory {
    override fun create(
        dryRun: Boolean,
        artifactsDir: Path,
        githubConfig: GitHubConfig,
    ): Publisher = mockk(relaxUnitFun = relaxUnitFun)
}
