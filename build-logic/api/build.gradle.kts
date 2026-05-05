plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.freecam.publish.schema)
    api(libs.kotlin.semver)
    implementation(libs.kotlin.serialization.json)
}
