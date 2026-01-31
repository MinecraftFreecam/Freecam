import net.xolt.freecam.gradle.plugin

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.foojay.resolver))
    implementation(libs.kotlin.serialization.toml)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}