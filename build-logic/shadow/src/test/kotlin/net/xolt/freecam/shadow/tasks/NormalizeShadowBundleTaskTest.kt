package net.xolt.freecam.shadow.tasks

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NormalizeShadowBundleTaskTest {

    @Test
    fun `namePrefix extracts mod name by dropping numeric semantic version suffixes`() {
        "cloth-config-5.3.63".namePrefix shouldBe "cloth-config"
        "cloth-config-forge-5.3.63".namePrefix shouldBe "cloth-config-forge"
        "jei-1.17.1-8.3.0.0".namePrefix shouldBe "jei"
        "foo-v2-1.11".namePrefix shouldBe "foo-v2"
        "".namePrefix shouldBe ""

        withClue("No version or segments") {
            "freecam".namePrefix shouldBe "freecam"
        }
    }
}
