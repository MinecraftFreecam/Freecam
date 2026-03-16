import net.xolt.freecam.gradle.ProjectReleaseMetadataTask

plugins {
    id("freecam.common")
}

sourceSets {
    main {
        // Manually depend on common's pre-processed sources.
        // NOTE: loaders have no build dependency on common, so API/implementation
        //  classes and transitive dependencies must be manually propagated.
        val commonTasks = commonNode.project.tasks
        java.srcDirs(commonTasks.named<Sync>("stonecutterGenerate").map { it.destinationDir })
        resources.srcDirs(commonTasks.processResources.map { it.destinationDir })
    }
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