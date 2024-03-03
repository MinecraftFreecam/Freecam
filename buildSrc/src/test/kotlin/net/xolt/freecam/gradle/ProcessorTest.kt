package net.xolt.freecam.gradle

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.function.Executable
import kotlin.test.assertEquals

/**
 * Declares a test for [LangProcessor] behavior.
 */
internal data class ProcessorTest(
    val name: String,
    val modID: String = "modid",
    val variant: String = "normal",
    val translations: Map<String, String>,
    val fallback: Map<String, String>? = null,
    val result: Map<String, String>,
) {
    fun buildTest(test: Executable): DynamicTest = DynamicTest.dynamicTest(name, test)
    fun buildProcessorTest(processor: LangProcessor): DynamicTest = this.buildTest {
        assertEquals(result, processor.process(modID, variant, translations, fallback))
    }
}