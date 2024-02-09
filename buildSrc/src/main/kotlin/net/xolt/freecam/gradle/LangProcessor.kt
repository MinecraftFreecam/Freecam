package net.xolt.freecam.gradle

internal fun interface LangProcessor {
    fun process(
        modID: String,
        variant: String,
        translations: Map<String, String>,
        fallback: Map<String, String>?
    ): Map<String, String>
}
