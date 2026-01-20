plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(plugin(libs.plugins.jetbrains.changelog))
    implementation(plugin(libs.plugins.modpublisher))
    testImplementation(kotlin("test"))
    implementation("dev.kikugie:stonecutter:0.8.2")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.firstdark.dev/releases/")
    maven("https://maven.firstdark.dev/snapshots/")
    maven("https://maven.kikugie.dev/snapshots")
}

tasks.test {
    useJUnitPlatform()
}

/**
 * Transforms a Version Catalog's Gradle Plugin alias into normal dependency notation.
 */
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }