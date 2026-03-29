plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

version = meta.version
group = meta.group

application {
    mainClass = "net.xolt.freecam.publish.MainKt"
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":publish:api"))
    implementation(project(":publish:cli"))
    implementation(project(":publish:platforms"))
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    testImplementation(libs.kotlin.test)
    testImplementation(testFixtures(project(":publish:api")))
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
}

tasks {
    processResources {
        // Include release-metadata.json from rootProject's :generateReleaseMetadata
        val generateReleaseMetadata by rootProject.tasks.existing
        dependsOn(generateReleaseMetadata)
        from(generateReleaseMetadata.map { it.outputs.files }) {
            rename { "release-metadata.json" }
        }
    }

    jar {
        from("LICENSE")
    }

    test {
        useJUnitPlatform()

        // Run all :publish:* tests
        dependsOn(provider {
            subprojects.mapNotNull { it.tasks.findByName("test") }
        })
    }
}
