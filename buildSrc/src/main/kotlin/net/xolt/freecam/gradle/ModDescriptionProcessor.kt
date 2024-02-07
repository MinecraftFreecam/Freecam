package net.xolt.freecam.gradle

internal class ModDescriptionProcessor : LangProcessor {
    override fun process(
        modID: String,
        variant: String,
        translations: Map<String, String>,
        fallback: Map<String, String>?
    ): Map<String, String> {
        val firstID = "${modID}.description"
        val secondID = "${modID}.description.${variant}"
        val ids = listOf(firstID, secondID)

        // Nothing to do if this language has no "description" translations
        if (ids.none(translations.keys::contains)) {
            return translations
        }

        val map = translations.toMutableMap()

        // Remove any description.variant keys
        map.keys
            .filter { it.startsWith("${firstID}.") }
            .forEach(map::remove)

        // Set modmenu summary if this language has a translation for firstID
        translations[firstID]?.let { map["modmenu.summaryTranslation.${modID}"] = it }

        // Set "full" description
        // Use fallback if either part is missing from this language
        ids.mapNotNull { translations[it] ?: fallback?.get(it) }
            .joinToString(" ")
            .let { description ->
                map[firstID] = description
                map["modmenu.descriptionTranslation.${modID}"] = description
            }

        return map
    }
}
