plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":api"))
    implementation(libs.plugins.stonecutter.coords)
    implementation(libs.kotlin.serialization.toml)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
}

gradlePlugin {
    plugins {
        create("modMetadata") {
            id = "freecam.modmetadata"
            implementationClass = "net.xolt.freecam.gradle.ModMetadataPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

