package net.xolt.freecam.publish.logging

fun interface LogRenderer {

    fun render(event: LogEvent): String

    operator fun invoke(event: LogEvent) = render(event)
}

object DefaultLogRenderer : AbstractLogRenderer()

abstract class AbstractLogRenderer : LogRenderer {

    protected open fun renderPrefix(event: LogEvent) =
        event.scopes.joinToString("") { "[$it] " }

    protected open fun renderMessage(event: LogEvent) =
        event.message

    override fun render(event: LogEvent) = buildString {
        // Append prefix
        append(renderPrefix(event))
        val indent = length

        // Append indented message lines
        val lines = renderMessage(event).lineSequence()
        append(lines.first())
        lines.drop(1).forEach {
            appendLine()
            if (it.isNotEmpty()) {
                repeat(indent) { append(' ') }
                append(it)
            }
        }
    }
}