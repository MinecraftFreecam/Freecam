package samples

import net.xolt.freecam.publish.logging.LogLevel
import net.xolt.freecam.publish.logging.Logger

internal object LoggerSamples {

    fun levelUsage(logger: Logger) {
        logger.threshold = LogLevel.INFO

        // Logs at the threshold
        logger.info { "info is printed" }

        // Logs when below threshold
        logger.warn { "warn is printed" }
        logger.error { "error is printed" }

        // Does not log when above threshold
        logger.debug { "debug is NOT printed" }
        logger.trace { "trace is NOT printed" }
    }
}