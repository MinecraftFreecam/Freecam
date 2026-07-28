import net.xolt.freecam.gradle.ReleaseMetadataTask

val cfg = configurations.create("releaseMetadata") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks.register<ReleaseMetadataTask>("generateReleaseMetadata") {
    group = "publishing"
    description = "Generates release metadata for publishing"

    projectMetadataFiles = cfg
}
