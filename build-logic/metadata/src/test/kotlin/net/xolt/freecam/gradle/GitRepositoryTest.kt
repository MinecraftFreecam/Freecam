package net.xolt.freecam.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.BeforeTest
import kotlin.test.Test

class GitRepositoryTest {

    private lateinit var rootDir: File
    private lateinit var cmdRunner: CommandRunner
    private lateinit var repository: GitRepository

    @BeforeTest
    fun setup() {
        cmdRunner = mockk()
        rootDir = createTempDirectory("git-repo-test").toFile().apply { deleteOnExit() }
        repository = GitRepository(rootDir, cmdRunner)
    }

    @Test
    fun `returns failure when git binary is missing from PATH`() {
        // CommandRunner returns null when git can't be executed
        every { cmdRunner(rootDir, "git", "rev-parse", "--show-toplevel") } returns null

        val result = repository.resolveMetadata()

        result.shouldBeFailure {
            it.message shouldBe "Could not execute git"
        }
    }

    @Test
    fun `returns failure when project directory is not a git repository`() {
        every { cmdRunner(rootDir, "git", "rev-parse", "--show-toplevel") } returns CommandRunner.Result(
            exitCode = 128,
            stdout = null,
            stderr = "fatal: not a git repository (or any of the parent directories)",
        )

        val result = repository.resolveMetadata()

        result.shouldBeFailure {
            it.shouldBeInstanceOf<IllegalStateException>()
            it.message shouldBe "Not a git repository"
        }
    }

    @Test
    fun `throws IllegalStateException on unexpected git CLI errors`() {
        every { cmdRunner(rootDir, "git", "rev-parse", "--show-toplevel") } returns CommandRunner.Result(
            exitCode = 1,
            stdout = null,
            stderr = "some internal git explosion",
        )

        val exception = shouldThrow<IllegalStateException> {
            repository.resolveMetadata()
        }
        exception.message shouldBe "Unknown git error (1): some internal git explosion"
    }

    @Test
    fun `returns failure when project is nested inside a different git repository`() {
        val otherDir = createTempDirectory("different-git-repo").toFile().apply { deleteOnExit() }

        every { cmdRunner(rootDir, "git", "rev-parse", "--show-toplevel") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = otherDir.canonicalPath,
            stderr = null,
        )

        val result = repository.resolveMetadata()

        result.shouldBeFailure {
            it.message shouldBe "Git repo ($otherDir) is not project ($rootDir)."
        }
    }

    @Test
    fun `returns metadata successfully for clean git repository state`() {
        every { cmdRunner(rootDir, "git", "rev-parse", "--show-toplevel") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = rootDir.canonicalPath,
            stderr = null,
        )
        every { cmdRunner(rootDir, "git", "rev-parse", "--short", "HEAD") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = "b4df00d\n",
            stderr = "",
        )
        every { cmdRunner(rootDir, "git", "status", "--porcelain") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = "",
            stderr = "",
        )

        val result = repository.resolveMetadata()

        result.shouldBeSuccess {
            it.revision shouldBe "b4df00d"
            it.isDirty.shouldBeFalse()
        }
    }

    @Test
    fun `returns metadata successfully and marks dirty flag when modifications exist`() {
        every { cmdRunner(rootDir, "git", "rev-parse", "--show-toplevel") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = rootDir.canonicalPath,
            stderr = "",
        )
        every { cmdRunner(rootDir, "git", "rev-parse", "--short", "HEAD") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = "a1b2c3d",
            stderr = "",
        )
        every { cmdRunner(rootDir, "git", "status", "--porcelain") } returns CommandRunner.Result(
            exitCode = 0,
            stdout = " M src/main/kotlin/Plugin.kt\n?? newfile.txt",
            stderr = "",
        )

        val result = repository.resolveMetadata()

        result.shouldBeSuccess {
            it.revision shouldBe "a1b2c3d"
            it.isDirty.shouldBeTrue()
        }
    }
}
