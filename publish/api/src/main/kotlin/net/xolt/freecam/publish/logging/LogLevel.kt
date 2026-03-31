package net.xolt.freecam.publish.logging

enum class LogLevel {
    /** Silent. */
    NONE,
    /** Fatal errors and unrecoverable issues. */
    ERROR,
    /** Unexpected but recoverable issues. */
    WARNING,
    /** Normal operational output. */
    INFO,
    /** Useful debugging output. */
    DEBUG,
    /** Verbose diagnostic data. */
    TRACE,
}

/**
 * Whether this level should typically output to [stdout][System.out] or [stderr][System.err].
 *
 * Follows Unix CLI conventions:
 *   - stdout → normal user-facing output
 *   - stderr → diagnostics (warnings, errors, debug, trace)
 */
val LogLevel.isStderr: Boolean
    get() = when (this) {
        LogLevel.ERROR,
        LogLevel.WARNING,
        LogLevel.DEBUG,
        LogLevel.TRACE -> true
        LogLevel.INFO,
        LogLevel.NONE -> false
    }

operator fun LogLevel.plus(increment: Int) = shift(increment)
operator fun LogLevel.minus(decrement: Int) = shift(-decrement)

private fun LogLevel.shift(delta: Int): LogLevel = LogLevel.entries[
    (ordinal + delta).coerceIn(0, LogLevel.entries.lastIndex)
]