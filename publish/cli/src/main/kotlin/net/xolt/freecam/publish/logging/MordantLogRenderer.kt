package net.xolt.freecam.publish.logging

import com.github.ajalt.clikt.core.BaseCliktCommand
import com.github.ajalt.colormath.model.Ansi16
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.terminal.Terminal

/**
 * Format log messages using Mordant text styling.
 *
 * This directly applies [Ansi16]-level [styles][TextStyle], which are implemented using the
 * _ECMA-48 Select Graphic Rendition_ spec.
 *
 * If the output is printed using a Mordant [Terminal],
 * it may translate or strip ANSI styles depending on platform capabilities.
 *
 * The full Clikt library implements [BaseCliktCommand.echo] with a Mordant [Terminal],
 * but the lightweight [core-module](https://ajalt.github.io/clikt/advanced/#core-module) library does not.
 *
 * - [Mordant](https://ajalt.github.io/mordant)
 * - [ECMA-48](https://www.ecma-international.org/wp-content/uploads/ECMA-48_5th_edition_june_1991.pdf)
 * - [Clikt](https://ajalt.github.io/clikt)
 */
object MordantLogRenderer : AbstractLogRenderer() {

    override fun renderPrefix(event: LogEvent) =
        gray(super.renderPrefix(event))

    override fun renderMessage(event: LogEvent): String {
        val style: TextStyle? = when (event.level) {
            LogLevel.ERROR -> bold + red
            LogLevel.WARNING -> bold + yellow
            LogLevel.TRACE -> gray
            else -> null
        }

        val text = super.renderMessage(event)
        return style?.let { it(text) } ?: text
    }
}