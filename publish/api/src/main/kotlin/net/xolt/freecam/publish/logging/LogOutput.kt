package net.xolt.freecam.publish.logging

typealias LogOutput = LogEvent.(rendered: String) -> Unit

/**
 * A [LogOutput] that prints to stdout or stderr based on [LogLevel.isStderr].
 */
val PrintLogOutput: LogOutput = { rendered ->
    level.output.println(rendered)
}

private val LogLevel.output
    get() = if (isStderr) System.err else System.out