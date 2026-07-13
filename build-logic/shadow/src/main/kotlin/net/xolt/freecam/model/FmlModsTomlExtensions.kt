package net.xolt.freecam.model

/**
 * Merge nested data from two [FmlModsToml]s.
 *
 * Top-level fields from this [FmlModsToml] are kept as-is, while "nested" fields are merged:
 * - [mods]
 * - [dependencies]
 * - [mixins]
 * - [accessTransformers]
 *
 * Top-level defaults (like [logoFile]) are pushed down using [pushDownDefaults] to preserve semantics.
 */
internal infix fun FmlModsToml.mergeWith(incoming: FmlModsToml): FmlModsToml {
    val self = this.pushDownDefaults()
    val other = incoming.pushDownDefaults()
    return self.copy(
        mods = self.mods + other.mods,
        dependencies = mergeDependencies(self.dependencies, other.dependencies),
        mixins = mergeLists(self.mixins, other.mixins),
        accessTransformers = mergeLists(self.accessTransformers, other.accessTransformers),
    )
}

/**
 * Merges nullable dependency maps.
 * Combines keys and concatenates matching mod ID lists.
 */
private fun mergeDependencies(
    base: Map<String, List<FmlDependencyEntry>>?,
    incoming: Map<String, List<FmlDependencyEntry>>?
): Map<String, List<FmlDependencyEntry>>? {
    if (base == null && incoming == null) return null
    if (base == null) return incoming
    if (incoming == null) return base

    val allKeys = base.keys + incoming.keys
    return allKeys.associateWith { modId ->
        (base[modId] ?: emptyList()) + (incoming[modId] ?: emptyList())
    }
}

/**
 * Merges nullable lists.
 */
private fun <T> mergeLists(base: List<T>?, incoming: List<T>?): List<T>? {
    if (base == null && incoming == null) return null
    return (base ?: emptyList()) + (incoming ?: emptyList())
}
