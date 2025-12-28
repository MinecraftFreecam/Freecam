plugins {
    `kotlin-dsl`
}

dependencies {
    testImplementation(kotlin("test"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

/**
 * Transforms a Version Catalog's Gradle Plugin alias into normal dependency notation.
 */
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }