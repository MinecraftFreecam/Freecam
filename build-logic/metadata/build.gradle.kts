plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":api"))
    implementation(libs.plugins.stonecutter.coords)
    implementation(libs.kotlin.serialization.toml)
}

gradlePlugin {
    plugins {
        create("modMetadata") {
            id = "freecam.modmetadata"
            implementationClass = "net.xolt.freecam.gradle.ModMetadataPlugin"
        }
    }
}