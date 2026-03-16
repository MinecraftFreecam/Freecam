import net.xolt.freecam.gradle.plugin

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":api"))
    implementation(project(":release-metadata"))
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.jetbrains.changelog))
    implementation(libs.mapping.io)
    implementation(libs.kotlin.serialization.json)
}