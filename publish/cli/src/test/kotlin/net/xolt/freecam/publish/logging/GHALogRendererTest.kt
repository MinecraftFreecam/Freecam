package net.xolt.freecam.publish.logging

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GHALogRendererTest {

    @Test
    fun `error message gets error annotation`() {
        val ctx = testLogMessage(level = LogLevel.ERROR, message = "failure")

        val result = GHALogRenderer(ctx)

        result shouldBe "::error::failure"
    }

    @Test
    fun `debug message gets debug annotation`() {
        val ctx = testLogMessage(level = LogLevel.DEBUG, message = "debug info")

        val result = GHALogRenderer(ctx)

        result shouldBe "::debug::debug info"
    }

    @Test
    fun `normal message unchanged`() {
        val ctx = testLogMessage(level = LogLevel.INFO, message = "hello")

        val result = GHALogRenderer(ctx)

        result shouldBe "hello"
    }

    @Test
    fun `GHA annotation is before scope prefix`() {
        val messages = listOf(
            testLogMessage(level = LogLevel.ERROR, message = "msg1", scopes = listOf("a")),
            testLogMessage(level = LogLevel.WARNING, message = "msg2", scopes = listOf("b")),
            testLogMessage(level = LogLevel.INFO, message = "msg3", scopes = listOf("c")),
        )

        val result = messages.map(GHALogRenderer::invoke)

        result shouldContainExactly listOf(
            "::error::[a] msg1",
            "::warning::[b] msg2",
            "[c] msg3",
        )
    }
}