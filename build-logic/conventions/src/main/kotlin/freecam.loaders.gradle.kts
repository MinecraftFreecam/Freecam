import net.xolt.freecam.gradle.ProjectReleaseMetadataTask

plugins {
    id("freecam.common")
}

dependencies {
    implementation(commonNode.project)
}

tasks.processResources {
    // We do some loader-specific processing in processResources...
    // it's a little ugly that we add resources via `implementation` _and_ processResources
    // FIXME: decide whether we will process in :common or loaders and stick to one
    from(commonNode.project.tasks.processResources)
}

tasks.register<ProjectReleaseMetadataTask>("generateReleaseMetadata") {
    group = "publishing"
    description = "Generates release metadata for publishing"
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}