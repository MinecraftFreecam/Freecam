import net.xolt.freecam.gradle.ProjectReleaseMetadataTask

plugins {
    id("freecam.common")
}

val extraJavaSourceDirs = configurations.create("extraJavaSourceDirs") {
    description = "Additional Java source directories added to the main source set."
    isCanBeConsumed = false
    isCanBeResolved = true
}

val extraResourceDirs = configurations.create("extraResourceDirs") {
    description = "Additional resource directories added to the main source set."
    isCanBeConsumed = false
    isCanBeResolved = true
}

sourceSets {
    main {
        java.srcDirs(extraJavaSourceDirs)
        resources.srcDirs(extraResourceDirs)
    }
}

dependencies {
    val commonPath = commonNode.project.path
    // Manually depend on common's pre-processed sources.
    // NOTE: loaders have no build dependency on common, so API/implementation
    //  classes and transitive dependencies must be manually propagated.
    extraJavaSourceDirs(project(path = commonPath, configuration = "generatedSourcesElements"))
    extraResourceDirs(project(path = commonPath, configuration = "processedResourcesElements"))
}

val releaseMetadataTask = tasks.register<ProjectReleaseMetadataTask>("generateReleaseMetadata") {
    group = "publishing"
    description = "Generates release metadata for publishing"
}

configurations.create("releaseMetadataElements") {
    isCanBeConsumed = true
    isCanBeResolved = false

    artifacts {
        add(name, releaseMetadataTask.flatMap { it.outputFile })
    }
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
