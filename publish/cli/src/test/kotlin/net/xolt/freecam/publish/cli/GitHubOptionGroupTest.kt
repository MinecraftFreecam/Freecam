package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.testing.CliktCommandTestResult
import com.github.ajalt.clikt.testing.test
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class GitHubOptionGroupTest {

    private fun githubEnv(
        token: String? = "token",
        sha: String? = "deadbeef",
        owner: String? = "owner",
        repository: String? = "owner/repo",
    ) = buildMap {
        token?.let { put("GITHUB_TOKEN", it) }
        sha?.let { put("GITHUB_SHA", it) }
        owner?.let { put("GITHUB_REPOSITORY_OWNER", it) }
        repository?.let { put("GITHUB_REPOSITORY", it) }
    }

    @Test
    fun `toString does not leak token`() {
        val cmd = TestCommand()
        cmd.test(listOf(
            "--gh-token", "abcdefghijklmnopqrstuvwxyz",
            "--gh-owner", "owner",
            "--gh-repo", "repo",
            "--git-sha", "sha",
        ))
        val result = cmd.github.toString()

        result shouldBe "GitHubOptionGroup(token='abcd******', owner='owner', repo='repo', headSha='sha')"
    }

    @Test
    fun `valid GitHub options succeed`() {
        val fixtures = listOf(
            ValidOptionFixture(
                clue = "CLI args populate all fields",
                env = emptyMap(),
                args = listOf("--gh-token", "token", "--gh-owner", "owner", "--gh-repo", "repo", "--git-sha", "deadbeef"),
                expectedOwner = "owner",
                expectedRepo = "repo",
            ),
            ValidOptionFixture(
                clue = "Env vars populate fields, repo trimmed from owner/repo",
                env = githubEnv(),
                args = emptyList(),
                expectedOwner = "owner",
                expectedRepo = "repo",
            ),
            ValidOptionFixture(
                clue = "CLI owner/repo override env fallback",
                env = githubEnv(),
                args = listOf("--gh-owner", "explicit-owner", "--gh-repo", "explicit-repo"),
                expectedOwner = "explicit-owner",
                expectedRepo = "explicit-repo",
            )
        )

        fixtures.testEach { cmd, result ->
            result.statusCode shouldBe 0
            cmd.github.token shouldBe expectedToken
            cmd.github.owner shouldBe expectedOwner
            cmd.github.repo shouldBe expectedRepo
            cmd.github.headSha shouldBe expectedSha
        }
    }

    @Test
    fun `repo splitting logic works for env vs CLI`() {
        val fixtures = listOf(
            RepoOwnerFixture(
                clue = "repo from env trims owner",
                env = githubEnv(repository = "owner/repo"),
                args = emptyList(),
                expectedOwner = "owner",
                expectedRepo = "repo",
            ),
            RepoOwnerFixture(
                clue = "repo CLI containing '/' fails",
                env = githubEnv(),
                args = listOf("--gh-repo", "owner/repo"),
                expectedOwner = "owner",
                expectedRepo = "owner/repo",
                expectedError = "--gh-repo",
            ),
            RepoOwnerFixture(
                clue = "owner from env is untouched",
                env = githubEnv(owner = "some-owner"),
                args = emptyList(),
                expectedOwner = "some-owner",
                expectedRepo = "repo",
            ),
            RepoOwnerFixture(
                clue = "owner CLI containing '/' fails",
                env = githubEnv(),
                args = listOf("--gh-owner", "owner/name"),
                expectedOwner = "owner/name",
                expectedRepo = "repo",
                expectedError = "--gh-owner",
            )
        )

        fixtures.testEach { cmd, result ->
            expectedError?.let {
                result.statusCode shouldBe 1
                result.stderr shouldContain it
            } ?: run {
                result.statusCode shouldBe 0
                cmd.github.owner shouldBe expectedOwner
                cmd.github.repo shouldBe expectedRepo
            }
        }
    }

    @Test
    fun `missing or invalid GitHub options fail`() {
        val fixtures = listOf(
            InvalidOptionFixture(
                clue = "missing token",
                env = githubEnv(token = null),
                args = emptyList(),
                expectedError = "--gh-token",
            ),
            InvalidOptionFixture(
                clue = "missing owner",
                env = githubEnv(owner = null),
                args = emptyList(),
                expectedError = "--gh-owner",
            ),
            InvalidOptionFixture(
                clue = "missing repo",
                env = githubEnv(repository = null),
                args = emptyList(),
                expectedError = "--gh-repo",
            ),
            InvalidOptionFixture(
                clue = "repo CLI contains '/'",
                env = githubEnv(),
                args = listOf("--gh-repo", "owner/repo"),
                expectedError = "--gh-repo",
            ),
            InvalidOptionFixture(
                clue = "owner CLI contains '/'",
                env = githubEnv(),
                args = listOf("--gh-owner", "owner/name"),
                expectedError = "--gh-owner",
            ),
            InvalidOptionFixture(
                clue = "repo CLI blank",
                env = githubEnv(),
                args = listOf("--gh-repo", ""),
                expectedError = "--gh-repo",
            ),
            InvalidOptionFixture(
                clue = "owner CLI blank",
                env = githubEnv(),
                args = listOf("--gh-owner", ""),
                expectedError = "--gh-owner",
            )
        )

        fixtures.testEach { cmd, result ->
            result.statusCode shouldBe 1
            result.stderr shouldContain expectedError
        }
    }

    private class TestCommand : CliktCommand() {
        val github by GitHubOptionGroup()
        override fun run() = Unit
    }

    private sealed interface GitHubTestFixture {
        val clue: String
        val args: List<String>
        val env: Map<String, String>
    }

    private data class ValidOptionFixture(
        override val clue: String,
        override val env: Map<String, String>,
        override val args: List<String>,
        val expectedOwner: String,
        val expectedRepo: String,
        val expectedToken: String = "token",
        val expectedSha: String = "deadbeef",
    ) : GitHubTestFixture

    private data class RepoOwnerFixture(
        override val clue: String,
        override val env: Map<String, String>,
        override val args: List<String>,
        val expectedOwner: String,
        val expectedRepo: String,
        val expectedError: String? = null,
    ) : GitHubTestFixture

    private data class InvalidOptionFixture(
        override val clue: String,
        override val env: Map<String, String>,
        override val args: List<String>,
        val expectedError: String
    ) : GitHubTestFixture

    private fun <T : GitHubTestFixture> Iterable<T>.testEach(
        block: T.(cmd: TestCommand, result: CliktCommandTestResult) -> Unit,
    ) = assertSoftly {
        forEach {
            withClue(it.clue) {
                val cmd = TestCommand()
                val result = cmd.test(it.args, envvars = it.env)
                it.block(cmd, result)
            }
        }
    }
}
