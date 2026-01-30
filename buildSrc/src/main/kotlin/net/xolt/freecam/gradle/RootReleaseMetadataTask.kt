package net.xolt.freecam.gradle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.model.Platforms
import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.ReleaseMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.util.internal.VersionNumber

/**
 * Aggregates all per-project ReleaseMetadata files into a single root ReleaseMetadata JSON file.
 */
abstract class RootReleaseMetadataTask : DefaultTask() {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @get:Input
    abstract val changelog: Property<String>

    @get:InputFiles
    abstract val projectMetadataFiles: ListProperty<RegularFile>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.convention(
            project.layout.buildDirectory.file("release-metadata/${project.property("mod.version")}.json")
        )
    }

    @TaskAction
    @OptIn(ExperimentalSerializationApi::class)
    fun aggregate() {
        val projectReleaseMetadata = runBlocking {
            projectMetadataFiles.get()
                .asSequence()
                .map { it.asFile }
                .filter { it.exists() }
                .map { file ->
                    async(Dispatchers.IO) { json.decodeFromStream<ProjectReleaseMetadata>(file.inputStream()) }
                }
                .toList()
                .awaitAll()
                .sortedWith(compareBy(
                    { VersionNumber.parse(it.minecraft) },
                    { it.minecraft },
                    { it.loader },
                ))
        }

        // Construct root ReleaseMetadata
        val rootMetadata = ReleaseMetadata(
            modVersion = project.property("mod.version") as String,
            releaseType = project.property("mod.release_type") as String,
            displayName = "${project.property("mod.name")} ${project.property("mod.version")}",
            changelog = changelog.get(),
            versions = projectReleaseMetadata,
            platforms = Platforms(
                curseforge = Platforms.Curseforge(project.property("mod.curseforge_id") as String),
                modrinth = Platforms.Modrinth(project.property("mod.modrinth_id") as String),
                github = Platforms.Github("v${project.property("mod.version")}"),
            )
        )

        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.outputStream().use { output ->
            json.encodeToStream<ReleaseMetadata>(rootMetadata, output)
        }
    }
}
