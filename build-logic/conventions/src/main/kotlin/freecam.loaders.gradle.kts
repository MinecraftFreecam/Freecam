import net.xolt.freecam.gradle.ProjectReleaseMetadataTask

plugins {
    id("freecam.common")
}

val extraJavaSources = configurations.create("extraJavaSources") {
    description = "Additional Java sources added to the main source set."
    isCanBeConsumed = false
    isCanBeResolved = true
}

val extraResources = configurations.create("extraResources") {
    description = "Additional resources added to the main source set."
    isCanBeConsumed = false
    isCanBeResolved = true
}

sourceSets {
    main {
        java.srcDirs(extraJavaSources)
        resources.srcDirs(extraResources)
    }
}

dependencies {
    val commonPath = commonNode.project.path
    // Manually depend on common's pre-processed sources.
    // NOTE: loaders have no build dependency on common, so API/implementation
    //  classes and transitive dependencies must be manually propagated.
    extraJavaSources(project(path = commonPath, configuration = "generatedSourcesElements"))
    extraResources(project(path = commonPath, configuration = "processedResourcesElements"))
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
