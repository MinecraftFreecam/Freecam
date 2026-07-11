plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":api"))
    implementation(libs.kotlin.serialization.toml)
}
