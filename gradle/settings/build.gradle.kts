plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.foojay.resolver))
    testImplementation(kotlin("test"))
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.kikugie.dev/releases")
}

tasks.test {
    useJUnitPlatform()
}

/**
 * Transforms a Version Catalog's Gradle Plugin alias into normal dependency notation.
 */
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
