import net.xolt.freecam.gradle.ProjectReleaseMetadataTask
import net.xolt.freecam.gradle.ReleaseMetadataTask
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

tasks.register<ReleaseMetadataTask>("generateReleaseMetadata") {
    group = "publishing"
    description = "Generates release metadata for publishing"

    projectMetadataFiles = subprojects.flatMap { subproject ->
        subproject.tasks.withType<ProjectReleaseMetadataTask>().map { it.outputFile.get() }
    }

    dependsOn(subprojects.flatMap { subproject ->
        subproject.tasks.withType<ProjectReleaseMetadataTask>()
    })
}
