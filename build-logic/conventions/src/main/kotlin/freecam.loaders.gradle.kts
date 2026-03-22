import net.xolt.freecam.gradle.ProjectReleaseMetadataTask

plugins {
    id("freecam.common")
}

dependencies {
    compileOnly(project(path = commonNode.project.path, configuration = "namedElements"))
}

tasks {
    processResources {
        from(commonNode.project.tasks.processResources)
    }

    jar {
        from(commonNode.project.tasks.compileJava)
    }

    register<ProjectReleaseMetadataTask>("generateReleaseMetadata") {
        group = "publishing"
        description = "Generates release metadata for publishing"
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