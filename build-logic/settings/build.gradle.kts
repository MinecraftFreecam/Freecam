plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":api"))
    implementation(libs.plugins.stonecutter.coords)
    implementation(libs.plugins.foojay.resolver.coords)
    implementation(libs.kotlin.serialization.toml)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}

gradlePlugin {
    plugins {
        create("modMetadata") {
            id = "freecam.modmetadata"
            implementationClass = "net.xolt.freecam.gradle.ModMetadataSettingsPlugin"
        }
    }
}