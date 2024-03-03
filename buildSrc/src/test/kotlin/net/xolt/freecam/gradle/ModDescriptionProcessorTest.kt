package net.xolt.freecam.gradle

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.BeforeTest

class ModDescriptionProcessorTest {

    private lateinit var processor: LangProcessor

    @BeforeTest
    fun setup() {
        processor = ModDescriptionProcessor()
    }

    @TestFactory
    fun `Basic tests`(): List<DynamicTest> {
        val sample1 = mapOf(
            "modid.description" to "Default description",
            "modid.description.special" to "(extra special)"
        )

        return listOf(
            ProcessorTest(
                name = "Discard variant descriptions",
                translations = sample1,
                result = mapOf(
                    "modid.description" to "Default description",
                    "modmenu.descriptionTranslation.modid" to "Default description",
                    "modmenu.summaryTranslation.modid" to "Default description"
                )
            ),
            ProcessorTest(
                name = "Append \"extra special\" to description",
                variant = "special",
                translations = sample1,
                result = mapOf(
                    "modid.description" to "Default description (extra special)",
                    "modmenu.descriptionTranslation.modid" to "Default description (extra special)",
                    "modmenu.summaryTranslation.modid" to "Default description"
                )
            )
        ).map { it.buildProcessorTest(processor) }
    }
}