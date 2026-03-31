import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.plugin.use.PluginDependency

/**
 * Represent a Version Catalog's Gradle [PluginDependency] in maven coordinate notation.
 */
val PluginDependency.coords: String
    get() = "$pluginId:$pluginId.gradle.plugin:$version"

/**
 * Map a Version Catalog's Gradle [PluginDependency] provider to maven coordinate notation.
 */
val Provider<PluginDependency>.coords: Provider<String>
    get() = map { it.coords }

/**
 * Map a Version Catalog's Gradle [PluginDependency] provider to maven coordinate notation.
 */
val ProviderConvertible<PluginDependency>.coords: Provider<String>
    get() = asProvider().coords
