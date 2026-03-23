plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":api"))
    implementation(project(":release-metadata"))
    implementation(libs.plugins.stonecutter.coords)
    implementation(libs.plugins.jetbrains.changelog.coords)
    implementation(libs.mapping.io)
    implementation(libs.kotlin.serialization.json)
}