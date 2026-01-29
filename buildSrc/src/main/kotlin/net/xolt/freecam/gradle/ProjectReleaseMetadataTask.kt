package net.xolt.freecam.gradle

import currentMod
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import mod
import net.xolt.freecam.model.ProjectReleaseMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named

abstract class ProjectReleaseMetadataTask : DefaultTask() {

    private val json = Json { ignoreUnknownKeys = true }

    private val java get() = project.extensions.getByType<JavaPluginExtension>()

    @get:Input
    abstract val artifactFileName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        artifactFileName.convention(
            project.tasks.named("jar", Jar::class).flatMap { it.archiveFileName }
        )
        outputFile.convention(
            artifactFileName.flatMap { name ->
                val base = name.removeSuffix(".jar")
                project.layout.buildDirectory.file("release-metadata/$base.json")
            }
        )
    }

    @TaskAction
    @OptIn(ExperimentalSerializationApi::class)
    fun generate() {
        val loader = project.name
        val mc = project.currentMod.mc

        val metadata = ProjectReleaseMetadata(
            loader = loader,
            minecraft = mc,
            filename = artifactFileName.get(),
            gameVersions = listOf(mc),
            javaVersions = listOf(java.targetCompatibility),
            // FIXME: neither `project.mod` nor `currentRootProject.mod` fully encapsulate the `gradle.properties` set
            // this is a fundamental limit of gradle's project path hierarchy. :fabric:1.21.11 needs to have _two_ parents;
            // :fabric and :1.21.11 - gradle only sees the former as a parent, so we represent the latter as currentRootProject
            relationships = project.mod.relationships,
        )

        outputFile.get().asFile.outputStream().use { output ->
            json.encodeToStream(metadata, output)
        }
    }
}
