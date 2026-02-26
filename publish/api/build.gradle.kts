plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-test-fixtures`
}

version = meta.version
group = meta.group

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":build-logic:api"))
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
}

tasks.test {
    useJUnitPlatform()
}
