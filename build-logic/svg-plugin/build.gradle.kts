plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(libs.batik.codec)
    implementation(libs.batik.transcoder)
    implementation(libs.pngtastic)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("svg") {
            id = "freecam.svg"
            implementationClass = "net.xolt.freecam.gradle.SvgPlugin"
        }
    }
}
