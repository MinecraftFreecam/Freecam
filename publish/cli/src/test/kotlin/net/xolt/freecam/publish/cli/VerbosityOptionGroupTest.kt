package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.testing.test
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.xolt.freecam.publish.logging.LogLevel
import net.xolt.freecam.publish.logging.plus
import kotlin.test.Test

class VerbosityOptionGroupTest {

    @Test
    fun `get expected verbosity from various args`() {
        listOf(
            TestFixture("INFO is default", LogLevel.INFO),
            TestFixture("--quiet is NONE", LogLevel.NONE, "--quiet"),
            TestFixture("--verbosity=quiet is NONE", LogLevel.NONE, "--verbosity=quiet"),
            TestFixture("NONE is not incremented", LogLevel.NONE, "--verbosity=quiet", "-vvv"),
            TestFixture("--quiet overrides --verbosity", LogLevel.NONE, "--quiet", "--verbosity=errors"),
            TestFixture("INFO is incremented", LogLevel.INFO + 3, "--verbosity=normal", "-vvv"),
            TestFixture("ERROR is incremented", LogLevel.ERROR + 3, "--verbosity=errors", "-vv", "--verbose"),
            TestFixture("Extra increments are ignored", LogLevel.entries.last(), "--verbosity=debug", "-vvvvvv"),
        ).assertSoftly {
            val cmd = TestCommand()
            val result = cmd.test(*args)
            result.statusCode shouldBe 0
            cmd.verbosity shouldBe expected
        }
    }

    private class TestCommand : CliktCommand() {
        val options by VerbosityOptionGroup()
        val verbosity get() = options.level
        override fun run() = Unit
    }

    private class TestFixture(
        val clue: String,
        val expected: LogLevel,
        vararg val args: String,
    )

    private fun Iterable<TestFixture>.assertSoftly(block: TestFixture.() -> Unit) {
        io.kotest.assertions.assertSoftly {
            forEach {
                withClue(it.clue) {
                    it.block()
                }
            }
        }
    }
}
