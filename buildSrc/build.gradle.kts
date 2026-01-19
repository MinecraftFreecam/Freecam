import java.util.Properties

plugins {
    `kotlin-dsl`
}

private val props = Properties().apply {
    load(rootDir.resolve("../gradle.properties").inputStream())
}

dependencies {
    implementation(plugin(libs.plugins.jetbrains.changelog))
    implementation(plugin(libs.plugins.modpublisher))
    testImplementation(kotlin("test"))
    implementation("dev.kikugie:stonecutter:${props.getProperty("stonecutter_version")}")
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