package net.xolt.freecam.publish.logging

/**
 * Annotate log messages with [GitHub Actions annotations](https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-commands).
 */
object GHALogRenderer : AbstractLogRenderer() {
    override fun render(event: LogEvent): String {
        val message = super.render(event)
        return when (event.level) {
            LogLevel.ERROR -> "::error::$message"
            LogLevel.WARNING -> "::warning::$message"
            LogLevel.DEBUG,
            LogLevel.TRACE -> "::debug::$message"
            // TODO: ::notice:: maybe for INFO ?
            else -> message
        }
    }
}