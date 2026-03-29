package net.xolt.freecam.publish.logging

import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import net.xolt.freecam.publish.logging.LogLevel.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogLevelEnabledTest {

    lateinit var logger: Logger

    @BeforeTest
    fun setup() {
        logger = TestLogger()
    }

    @Test
    fun `NONE is never enabled`() {
        logger.apply {
            threshold = NONE
            LogLevel.entries.forEach {
                it.enabled.shouldBeFalse()
            }
        }
    }

    @Test
    fun `levels are correctly enabled`() {
        logger.apply {
            threshold = INFO
            LogLevel.entries.forEach {
                when (it) {
                    INFO -> it.enabled.shouldBeTrue()
                    ERROR, WARNING -> withClue("errors are less verbose than INFO") {
                        it.enabled.shouldBeTrue()
                    }
                    DEBUG, TRACE -> withClue("debugs are more verbose than INFO") {
                        it.enabled.shouldBeFalse()
                    }
                    NONE -> withClue("NONE is never enabled") {
                        it.enabled.shouldBeFalse()
                    }
                }
            }
        }
    }
}
