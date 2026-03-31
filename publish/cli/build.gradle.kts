plugins {
    alias(libs.plugins.kotlin.jvm)
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
    implementation(project(":publish:api"))
    implementation(libs.clikt)
    implementation(libs.kotlin.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(testFixtures(project(":publish:api")))
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
