package net.xolt.freecam.publish.logging

import net.xolt.freecam.publish.logging.Logger.Companion.default


abstract class Logger {

    /** Scope tags added to log messages. */
    abstract val scopes: List<String>

    /**
     * The log level threshold.
     *
     * - Logs at this level, or a 'less verbose' level, are evaluated.
     * - Logs at a 'more verbose' level are skipped.
     *
     * Usually defaults to [LogLevel.INFO].
     * In which case, [info], [warn], and [error] logs are evaluated,
     * while [debug] and [trace] logs are not.
     *
     * @sample samples.LoggerSamples.levelUsage
     */
    abstract var threshold: LogLevel

    /**
     * Whether a message with this log level would currently be printed.
     *
     * [LogLevel.NONE] is never enabled.
     * Other levels are enabled at and below the current [threshold].
     */
    inline val LogLevel.enabled
        get() = threshold >= this && this > LogLevel.NONE

    /**
     * The renderer that will render logs.
     * When overriding, you should typically extend from [AbstractLogRenderer].
     *
     * Usually defaults to [DefaultLogRenderer].
     */
    abstract var renderer: LogRenderer

    /**
     * The output that log messages are written to.
     *
     * Usually defaults to [PrintLogOutput].
     */
    abstract var output: LogOutput

    /** Evaluates and logs [msg] if [LogLevel.ERROR] is [enabled]. */
    inline fun error(msg: () -> String) = log(LogLevel.ERROR, msg)

    /** Evaluates and logs [msg] if [LogLevel.WARNING] is [enabled]. */
    inline fun warn(msg: () -> String) = log(LogLevel.WARNING, msg)

    /** Evaluates and logs [msg] if [LogLevel.INFO] is [enabled]. */
    inline fun info(msg: () -> String) = log(LogLevel.INFO, msg)

    /** Evaluates and logs [msg] if [LogLevel.DEBUG] is [enabled]. */
    inline fun debug(msg: () -> String) = log(LogLevel.DEBUG, msg)

    /** Evaluates and logs [msg] if [LogLevel.TRACE] is [enabled]. */
    inline fun trace(msg: () -> String) = log(LogLevel.TRACE, msg)

    /** Evaluates and logs [msg] if [level] is currently [enabled]. */
    inline fun log(level: LogLevel = LogLevel.INFO, msg: () -> String) {
        if (level.enabled) {
            LogEvent(
                level = level,
                scopes = scopes,
                message = msg(),
            ).print()
        }
    }

    /**
     * Render and print this message, **without** checking if its log level is [enabled].
     * Mostly for internal use.
     *
     * @receiver the log message to print
     * @see [log]
     */
    fun LogEvent.print() {
        output(renderer(this))
    }

    /**
     * Create a scoped view of this logger.
     *
     * @param scope name of the scope, used in log prefix. E.g. `[scope]`
     * @param config optional configuration block applied to the scoped logger.
     */
    fun scoped(scope: String, config: Logger.() -> Unit = { }): Logger =
        ScopedLogger(this, scope).apply(config)

    companion object {

        /** Default logger. */
        val default: Logger = DefaultLogger()

        /** Configure the [default] logger. */
        fun configure(config: Logger.() -> Unit) {
            default.apply(config)
        }

        /**
         * Create a scoped view of the [default] logger.
         *
         * @param scope name of the scope, used in log prefix. E.g. `[scope]`
         * @param config optional configuration block applied to the scoped logger.
         */
        fun scoped(scope: String, config: Logger.() -> Unit = { }): Logger =
            default.scoped(scope, config)
    }
}
