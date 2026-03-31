package net.xolt.freecam.publish.logging

import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.logging.LogLevel as KtorLogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger

internal class KtorLoggingAdapter(
    private val level: LogLevel,
    private val logger: Logger,
) : KtorLogger {

    val enabled get() = logger.run {
        this@KtorLoggingAdapter.level.enabled
    }

    override fun log(message: String) {
        logger.log(level) { message }
    }
}

fun LoggingConfig.useLoggingAdapter(
    scope: String = "HTTP",
    level: LogLevel,
) {
    val adapter = KtorLoggingAdapter(
        level = level,
        logger = Logger.scoped(scope),
    )
    this.logger = adapter
    this.level =
        if (adapter.enabled) level.toKtorLogLevel()
        else KtorLogLevel.NONE
}

fun LogLevel.toKtorLogLevel(): KtorLogLevel = when (this) {
    LogLevel.NONE,
    LogLevel.ERROR,
    LogLevel.WARNING,
    LogLevel.INFO -> KtorLogLevel.NONE
    LogLevel.DEBUG -> KtorLogLevel.INFO
    LogLevel.TRACE -> KtorLogLevel.HEADERS
}