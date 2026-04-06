package net.xolt.freecam.util

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MergeMapsTest {

    @Test
    fun `mergeWith merges distinct keys`() {
        val a = mapOf("a" to "1")
        val b = mapOf("b" to "2")

        val result = a mergeWith b

        result shouldBe mapOf(
            "a" to "1",
            "b" to "2"
        )
    }

    @Test
    fun `mergeWith allows identical values`() {
        val a = mapOf("k" to "v")
        val b = mapOf("k" to "v")

        val result = a mergeWith b

        result shouldBe mapOf("k" to "v")
    }
}