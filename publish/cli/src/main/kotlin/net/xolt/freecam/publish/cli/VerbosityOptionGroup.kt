package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.enum
import net.xolt.freecam.publish.logging.LogLevel
import net.xolt.freecam.publish.logging.plus

internal class VerbosityOptionGroup : OptionGroup() {

    private val quiet by option(
        "-q", "--quiet",
        help = "Suppress output (shorthand for --verbosity=quiet)"
    ).flag()

    private val extraVerbosity by option("-v", "--verbose")
        .help("Increase verbosity level")
        .counted()

    private val verbosity by option("--verbosity")
        .help("Verbosity level")
        .enum<LogLevel> {
            when (it) {
                LogLevel.NONE -> "quiet"
                LogLevel.ERROR -> "errors"
                LogLevel.WARNING -> "warnings"
                LogLevel.INFO -> "normal"
                LogLevel.DEBUG -> "verbose"
                LogLevel.TRACE -> "debug"
            }
        }
        .defaultLazy(defaultForHelp = LogLevel.INFO.toString()) {
            if (quiet) LogLevel.NONE
            else if (ghaDebug) LogLevel.DEBUG
            else LogLevel.INFO
        }

    /**
     * Indicates that CI has [debug logging](https://docs.github.com/en/actions/how-tos/monitor-workflows/enable-debug-logging)
     * enabled.
     */
    private val ghaDebug by option(hidden = true, envvar = "RUNNER_DEBUG").flag()

    val level: LogLevel get() {
        return if (quiet || verbosity == LogLevel.NONE) LogLevel.NONE
        else verbosity + extraVerbosity
    }
}