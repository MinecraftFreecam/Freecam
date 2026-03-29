package net.xolt.freecam.publish.logging

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.BeforeTest
import kotlin.test.Test

class LoggerTest {

    lateinit var logger: TestLogger

    @BeforeTest
    fun setup() {
        logger = TestLogger()
    }

    @Test
    fun `message is evaluated lazily`() {
        logger.threshold = LogLevel.ERROR

        var evaluated = false

        logger.debug {
            evaluated = true
            "debug"
        }

        evaluated.shouldBeFalse()
    }

    @Test
    fun `info logs when level is INFO`() {
        logger.threshold = LogLevel.INFO

        logger.info { "hello" }

        logger.logs shouldContainExactly listOf(
            LogLevel.INFO to "hello",
        )
    }

    @Test
    fun `debug does not log when level is INFO`() {
        logger.threshold = LogLevel.INFO

        logger.debug { "debug message" }

        logger.logs.shouldBeEmpty()
    }

    @Test
    fun `debug logs when level is TRACE`() {
        logger.threshold = LogLevel.TRACE

        logger.debug { "debug message" }

        logger.logs shouldContainExactly listOf(
            LogLevel.DEBUG to "debug message",
        )
    }

    @Test
    fun `NONE disables all logging`() {
        logger.threshold = LogLevel.NONE

        logger.error { "error" }
        logger.info { "info" }
        logger.debug { "debug" }

        logger.messages.shouldBeEmpty()
    }
}
