package net.xolt.freecam.gradle

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.BeforeTest

class ModNameProcessorTest {

    private lateinit var processor: LangProcessor

    @BeforeTest
    fun setup() {
        processor = ModNameProcessor()
    }

    @TestFactory
    fun `Basic tests`(): List<DynamicTest> {
        val sample1 = mapOf(
            "modid.name" to "ModName",
            "modid.name.special" to "(extra special)"
        )

        return listOf(
            ProcessorTest(
                name = "Discard variant names",
                translations = sample1,
                result = mapOf(
                    "modid.name" to "ModName",
                    "modmenu.nameTranslation.modid" to "ModName"
                )
            ),
            ProcessorTest(
                name = "Append \"extra special\" to name",
                variant = "special",
                translations = sample1,
                result = mapOf(
                    "modid.name" to "ModName (extra special)",
                    "modmenu.nameTranslation.modid" to "ModName (extra special)"
                )
            )
        ).map { it.buildProcessorTest(processor) }
    }
}