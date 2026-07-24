package net.xolt.freecam.config.controller

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.xolt.freecam.config.model.ConfigLoader
import kotlin.test.Test

class CoreConfigControllerTest {

    @Test
    fun `getConfig before load caches and returns stable default`() {
        val loader = mockk<ConfigLoader<String>>()
        val controller = CoreConfigController(loader) { "default_value" }

        val firstCall = controller.config
        val secondCall = controller.config

        firstCall shouldBe "default_value"
        // Ensure the reference is cached and stable (avoids the desync bug)
        firstCall shouldBe secondCall
    }

    @Test
    fun `load delegates to loader and notifies listeners`() {
        val loader = mockk<ConfigLoader<String>>()
        every { loader.read() } returns "loaded_value"

        val controller = CoreConfigController(loader) { "default_value" }
        val listener = mockk<Runnable>(relaxed = true)
        controller.registerListener(listener)

        controller.load()

        controller.config shouldBe "loaded_value"
        verify(exactly = 1) { loader.read() }
        verify(exactly = 1) { listener.run() }
    }

    @Test
    fun `load failure falls back to default and still notifies listeners`() {
        val loader = mockk<ConfigLoader<String>>()
        every { loader.read() } throws RuntimeException("Simulated disk error")

        val controller = CoreConfigController(loader) { "default_value" }
        val listener = mockk<Runnable>(relaxed = true)
        controller.registerListener(listener)

        controller.load()

        // Should catch the error and fallback
        controller.config shouldBe "default_value"
        verify(exactly = 1) { listener.run() }
    }

    @Test
    fun `save delegates current config to loader and notifies listeners`() {
        val loader = mockk<ConfigLoader<String>>(relaxed = true)
        val controller = CoreConfigController(loader) { "default_value" }
        val listener = mockk<Runnable>(relaxed = true)
        controller.registerListener(listener)

        controller.save()

        verify(exactly = 1) { loader.write("default_value") }
        verify(exactly = 1) { listener.run() }
    }
}
