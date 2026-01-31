import net.xolt.freecam.gradle.plugin

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.foojay.resolver))
    implementation(libs.tomlj)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}