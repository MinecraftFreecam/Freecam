package net.xolt.freecam.gradle

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.plugin.use.PluginDependency
import org.gradle.api.provider.Provider

/**
 * Transforms a Version Catalog's Gradle Plugin alias into normal dependency notation.
 */
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>): Provider<String> = plugin.map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}