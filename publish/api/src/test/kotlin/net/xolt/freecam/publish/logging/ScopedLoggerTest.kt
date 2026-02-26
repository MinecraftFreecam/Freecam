package net.xolt.freecam.publish.logging

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.BeforeTest
import kotlin.test.Test

class ScopedLoggerTest {

    lateinit var logger: TestLogger

    @BeforeTest
    fun setup() {
        logger = TestLogger()
    }

    @Test
    fun `newlines align with prefix`() {
        logger.threshold = LogLevel.INFO
        val child = logger.scoped("child")

        logger.info { "hello\nworld" }
        child.info { "hello\nworld!" }
        child.info { "many\nmany\nlines\n!" }

        logger.messages shouldContainExactly listOf(
            "hello\nworld",
            "[child] hello\n        world!",
            """
                [child] many
                        many
                        lines
                        !
            """.trimIndent(),
        )
    }

    @Test
    fun `less verbose child overrides more verbose parent`() {
        logger.threshold = LogLevel.INFO
        val child = logger.scoped("child") {
            threshold = LogLevel.WARNING
        }

        logger.threshold shouldBe LogLevel.INFO
        child.threshold shouldBe LogLevel.WARNING
    }

    @Test
    fun `less verbose parent overrides more verbose child`() {
        logger.threshold = LogLevel.INFO
        val child = logger.scoped("child") {
            threshold = LogLevel.DEBUG
        }

        logger.threshold shouldBe LogLevel.INFO
        child.threshold shouldBe LogLevel.INFO
    }

    @Test
    fun `child renderer shadows parent's`() {
        val childRenderer = LogRenderer { TODO("Not implemented") }
        val parentRenderer = logger.renderer
        val child = logger.scoped("child") {
            renderer = childRenderer
        }

        logger.renderer shouldNotBeSameInstanceAs child.renderer
        logger.renderer shouldBeSameInstanceAs parentRenderer
        child.renderer shouldBeSameInstanceAs childRenderer
    }

    @Test
    fun `child output shadows parent's`() {
        val childOutput: LogOutput = { }
        val parentOutput: LogOutput = logger.output
        val child = logger.scoped("child") {
            output = childOutput
        }

        logger.output shouldNotBeSameInstanceAs child.output
        logger.output shouldBeSameInstanceAs parentOutput
        child.output shouldBeSameInstanceAs childOutput
    }
}