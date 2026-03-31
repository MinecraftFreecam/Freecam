plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.toml)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("i18n") {
            id = "freecam.i18n"
            implementationClass = "net.xolt.freecam.gradle.I18nPlugin"
        }
    }
}
