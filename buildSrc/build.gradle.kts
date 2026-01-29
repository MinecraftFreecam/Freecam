plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.jetbrains.changelog))
    implementation(libs.mapping.io)
    implementation(libs.kotlin.serialization.json)
    testImplementation(kotlin("test"))
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.firstdark.dev/releases/")
    maven("https://maven.firstdark.dev/snapshots/")
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.fabricmc.net/")
}

tasks.test {
    useJUnitPlatform()
}

/**
 * Transforms a Version Catalog's Gradle Plugin alias into normal dependency notation.
 */
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }