package net.xolt.freecam.i18n


private val ISO_LOCALE_CODE = "[a-z]{2,3}[-_][A-Z]{2,3}".toRegex()

/**
 * Whether this string is a valid locale code (e.g. `en-US` or `en_US`).
 *
 * True when this string is formatted as a two-letter or three-letter language code (ISO 639) and
 * a two-letter or three-letter country code (ISO 3166), separated by an underscore or hyphen.
 *
 * @see <a href="https://minecraft.wiki/w/Language#Languages">Minecraft languages</a>
 * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes">ISO 639 language codes</a>
 * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes">ISO 3166 country codes</a>
 */
internal val String.isLocaleCode: Boolean
    get() = matches(ISO_LOCALE_CODE)

/**
 * Represent an ISO locale code (e.g. `en-US`) as a Minecraft locale code (e.g. `en_us`).
 *
 * @see [String.isLocaleCode]
 */
internal val String.asMinecraftLocale: String get() {
    require(isLocaleCode) {
        "Invalid ISO locale code: $this"
    }
    return lowercase().replace('-', '_')
}