package net.xolt.freecam.i18n

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import net.xolt.freecam.util.mergeWith
import kotlin.test.Test

class WithErrorContextTest {

    @Test
    fun `withErrorContext includes context in error`() {
        val a = mapOf("k" to "A")
        val b = mapOf("k" to "B")

        val ex = shouldThrow<IllegalStateException> {
            withErrorContext({ "[en_US] merging test" }) {
                a mergeWith b
            }
        }

        ex.message shouldContain "[en_US] merging test"
        ex.message shouldContain "Conflicting translation for key 'k'"
    }
}