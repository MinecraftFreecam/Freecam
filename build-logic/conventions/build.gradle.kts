import net.xolt.freecam.gradle.plugin

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":api"))
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.jetbrains.changelog))
    implementation(plugin(libs.plugins.modpublisher))
    implementation(libs.mapping.io)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.tomlj)
}