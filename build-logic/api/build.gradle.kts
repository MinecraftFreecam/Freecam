plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.freecam.publish.schema)
    implementation(libs.kotlin.serialization.json)
}
