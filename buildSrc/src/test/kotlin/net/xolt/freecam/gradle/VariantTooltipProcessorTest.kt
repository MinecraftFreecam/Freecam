package net.xolt.freecam.gradle

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.BeforeTest

class VariantTooltipProcessorTest {

    private lateinit var processor: LangProcessor

    @BeforeTest
    fun setup() {
        processor = VariantTooltipProcessor()
    }

    @TestFactory
    fun `Basic tests`(): List<DynamicTest> {
        val twoVariants = mapOf(
            "foo.@NormalTooltip" to "normal",
            "foo.@SpecialTooltip" to "special"
        )

        return listOf(
            ProcessorTest(
                name = "Normal tooltip is used",
                translations = twoVariants,
                result = mapOf("foo.@Tooltip" to "normal")
            ),
            ProcessorTest(
                name = "Normal tooltip is removed",
                variant = "other",
                translations = twoVariants,
                result = emptyMap()
            ),
            ProcessorTest(
                name = "Special tooltip is used",
                variant = "special",
                translations = twoVariants,
                result = mapOf("foo.@Tooltip" to "special")
            )
        ).map { it.buildProcessorTest(processor) }
    }

}