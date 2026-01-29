plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":api"))
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
}