package net.xolt.freecam.publish.logging

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.bold
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MordantLogRendererTest {

    @Test
    fun `error message is bold and red`() {
        val ctx = testLogMessage(level = LogLevel.ERROR, message = "boom")
        val style = bold + red

        val result = MordantLogRenderer(ctx)

        result shouldBe style("boom")
    }

    @Test
    fun `warning message is bold and yellow`() {
        val ctx = testLogMessage(level = LogLevel.WARNING, message = "oops")
        val style = bold + yellow

        val result = MordantLogRenderer(ctx)

        result shouldBe style("oops")
    }

    @Test
    fun `trace message is gray`() {
        val ctx = testLogMessage(level = LogLevel.TRACE, message = "tracing")

        val result = MordantLogRenderer(ctx)

        result shouldBe gray("tracing")
    }

    @Test
    fun `info message unchanged`() {
        val ctx = testLogMessage(level = LogLevel.INFO, message = "hello")

        val result = MordantLogRenderer(ctx)

        result shouldBe "hello"
    }
}