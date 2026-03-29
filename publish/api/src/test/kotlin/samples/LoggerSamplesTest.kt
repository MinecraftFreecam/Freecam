package samples

import io.kotest.matchers.collections.shouldContainExactly
import net.xolt.freecam.publish.logging.LogLevel.*
import net.xolt.freecam.publish.logging.TestLogger
import kotlin.test.BeforeTest
import kotlin.test.Test

class LoggerSamplesTest {

    lateinit var logger: TestLogger

    @BeforeTest
    fun setup() {
        logger = TestLogger()
    }

    @Test
    fun level() {
        LoggerSamples.levelUsage(logger)
        logger.logs shouldContainExactly listOf(
            INFO to "info is printed",
            WARNING to "warn is printed",
            ERROR to "error is printed",
        )
    }
}