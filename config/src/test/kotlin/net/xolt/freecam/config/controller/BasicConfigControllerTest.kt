package net.xolt.freecam.config.controller

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*
import net.xolt.freecam.config.load.ConfigLoader
import kotlin.test.Test

class BasicConfigControllerTest {

    @Test
    fun `getConfig before load throws exception`() {
        val loader = mockk<ConfigLoader<String>>()
        val controller = BasicConfigController(loader) { "default_value" }

        val ex = shouldThrow<IllegalStateException> {
            controller.config
        }

        ex.message.shouldNotBeNull {
            shouldContain("before load")
        }
    }

    @Test
    fun `getConfig getConfig is idempotent`() {
        val loader = mockk<ConfigLoader<String>>()
        val controller = BasicConfigController(loader) { "default_value" }

        controller.load()
        val firstCall = controller.config
        val secondCall = controller.config

        firstCall shouldBe "default_value"
        firstCall shouldBeSameInstanceAs secondCall
    }

    @Test
    fun `load delegates to loader and notifies listeners`() {
        val loader = mockk<ConfigLoader<String>> {
            every { read() } returns "loaded_value"
        }

        val controller = BasicConfigController(loader) { "default_value" }
        val listener = mockk<Runnable> {
            every { run() } just Runs
        }

        controller.registerListener(listener)
        controller.load()

        controller.config shouldBe "loaded_value"
        verify(exactly = 1) { loader.read() }
        verify(exactly = 1) { listener.run() }
    }

    @Test
    fun `load failure falls back to default and still notifies listeners`() {
        val loader = mockk<ConfigLoader<String>> {
            every { read() } throws RuntimeException("Simulated disk error")
        }
        val controller = BasicConfigController(loader) { "default_value" }
        val listener = mockk<Runnable> {
            every { run() } just Runs
        }

        controller.registerListener(listener)
        controller.load()

        withClue("Should catch error and use default value") {
            controller.config shouldBe "default_value"
        }
        verify(exactly = 1) { listener.run() }
    }

    @Test
    fun `save writes current config to loader and notifies listeners`() {
        val loader = mockk<ConfigLoader<String>> {
            every { read() } returns "loaded_value"
        }
        val controller = BasicConfigController(loader) { "default_value" }
        val listener = mockk<Runnable> {
            every { run() } just Runs
        }

        controller.registerListener(listener)
        controller.load()
        controller.save()

        withClue("Should save the loaded value") {
            verify(exactly = 1) { loader.write("loaded_value") }
        }
        withClue("Listeners should be notified on both load and save") {
            verify(exactly = 2) { listener.run() }
        }
    }

    @Test
    fun `save before load is inert`() {
        val loader = mockk<ConfigLoader<String>>()
        val controller = BasicConfigController(loader) { "default_value" }
        val listener = mockk<Runnable>()

        controller.registerListener(listener)
        controller.save()

        // TODO: should this log an error/warning?
        withClue("Should have been inerted") {
            confirmVerified(loader, listener)
        }
    }
}
