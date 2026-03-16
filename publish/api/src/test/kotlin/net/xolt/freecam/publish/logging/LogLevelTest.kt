package net.xolt.freecam.publish.logging

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.xolt.freecam.publish.logging.LogLevel.*
import kotlin.test.Test

class LogLevelTest {

    @Test
    fun `NONE is the 'least verbose' level`() {
        LogLevel.entries.filterNot { it == NONE }.forEach {
            NONE shouldBeLessThan it
        }
    }

    @Test
    fun `errors are 'less verbose' than INFO`() {
        listOf(ERROR, WARNING).forEach {
            it shouldBeLessThan INFO
        }
    }

    @Test
    fun `debugs are 'more verbose' than INFO`() {
        listOf(DEBUG, TRACE).forEach {
            it shouldBeGreaterThan INFO
        }
    }

    @Test
    fun `plus increments level`() {
        ERROR + 1 shouldBe WARNING
    }

    @Test
    fun `minus decrements level`() {
        DEBUG - 1 shouldBe INFO
    }

    @Test
    fun `plus clamps to highest level`() {
        val highest = LogLevel.entries.last()
        highest + 5 shouldBe highest
    }

    @Test
    fun `minus clamps to lowest level`() {
        val lowest = LogLevel.entries.first()
        lowest - 5 shouldBe lowest
    }
}