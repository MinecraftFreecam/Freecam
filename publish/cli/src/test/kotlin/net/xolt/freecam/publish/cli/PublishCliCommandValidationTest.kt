package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.test.MetadataFixtures.testMetadata
import net.xolt.freecam.test.createTestFile
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class PublishCliCommandValidationTest {

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
        result.stderr shouldContain "${dir.absolute()} does not exist"
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
        result.stderr shouldContain "${file.absolute()} is not a directory"
    }
}
