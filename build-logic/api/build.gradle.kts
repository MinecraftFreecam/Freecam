plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlin.semver)
    implementation(libs.kotlin.serialization.json)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
}

tasks.test {
    useJUnitPlatform()
}