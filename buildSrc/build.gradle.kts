plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "1.9.20"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    testImplementation(kotlin("test"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
