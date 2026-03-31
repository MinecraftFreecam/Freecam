package net.xolt.freecam.util

/**
 * Merge two maps.
 * @throws IllegalStateException when both maps define a key with different values.
 */
internal infix fun <K, V> Map<K, V>.mergeWith(other: Map<K, V>) = mergeMaps(this, other)

/**
 * Merge two maps.
 * @throws IllegalStateException when both maps define a key with different values.
 */
internal fun <K, V> mergeMaps(original: Map<K, V>, other: Map<K, V>) = buildMap {
    putAll(original)

    other.forEach { (key, value) ->
        when (val existing = original[key]) {
            null -> put(key, value)
            value -> { /* equal value → no-op */ }
            else -> error(sequenceOf(
                "Conflicting translation for key '$key':",
                "  '$existing'",
                "  '$value'",
            ).joinToString("\n"))
        }
    }
}
