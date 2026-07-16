package net.xolt.freecam.util

import org.junit.jupiter.api.DisplayNameGenerator

class GsonVariantNameGenerator : DisplayNameGenerator.Standard() {
    private val gsonVersion = System.getProperty("gson.version")

    override fun generateDisplayNameForClass(testClass: Class<*>): String {
        val baseName = super.generateDisplayNameForClass(testClass)
        return gsonVersion?.let { "$baseName [Gson $it]" } ?: baseName
    }
}
