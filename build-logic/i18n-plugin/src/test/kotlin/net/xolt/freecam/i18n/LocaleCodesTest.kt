package net.xolt.freecam.i18n

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class LocaleCodesTest {

    @Test
    fun `valid locale is recognized`() {
        "en-US".isLocaleCode shouldBe true
        "en_US".isLocaleCode shouldBe true
    }

    @Test
    fun `invalid locale is rejected`() {
        "english".isLocaleCode shouldBe false
    }

    @Test
    fun `minecraft locale conversion`() {
        "en-US".asMinecraftLocale shouldBe "en_us"
        "en_US".asMinecraftLocale shouldBe "en_us"
    }
}