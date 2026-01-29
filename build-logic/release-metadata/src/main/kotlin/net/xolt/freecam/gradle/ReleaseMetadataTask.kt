package net.xolt.freecam.gradle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.model.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType

/**
 * Aggregates all per-project ReleaseMetadata files into a single root ReleaseMetadata JSON file.
 */
abstract class ReleaseMetadataTask : DefaultTask() {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @get:Input
    abstract val changelog: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val releaseType: Property<ReleaseType>

    @get:Input
    abstract val displayName: Property<String>

    @get:Input
    abstract val curseforgeId: Property<String>

    @get:Input
    abstract val modrinthId: Property<String>

    @get:Input
    abstract val githubTag: Property<String>

    @get:InputFiles
    abstract val projectMetadataFiles: ListProperty<RegularFile>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        val meta = project.provider {
            project.extensions.getByType<StaticModMetadata>()
        }
        version.convention(meta.map { it.version })
        releaseType.convention(meta.map { it.releaseType })
        displayName.convention(meta.map { "${it.name} ${it.version}" })
        curseforgeId.convention(meta.map { it.curseforgeId })
        modrinthId.convention(meta.map { it.modrinthId })
        githubTag.convention(meta.map { "v${it.version}" })
        changelog.convention("")
        projectMetadataFiles.convention(emptyList())
        outputFile.convention(version.flatMap {
            project.layout.buildDirectory.file("release-metadata/$it.json")
        })
    }

    @TaskAction
    fun run() {
        writeOutputJson(ReleaseMetadata(
            modVersion = version.get(),
            releaseType = releaseType.get(),
            displayName = displayName.get(),
            changelog = changelog.get(),
            versions = aggregateVersionFiles(),
            platforms = Platforms(
                curseforge = Platforms.Curseforge(curseforgeId.get()),
                modrinth = Platforms.Modrinth(modrinthId.get()),
                github = Platforms.Github(githubTag.get()),
            )
        ))
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun aggregateVersionFiles() = runBlocking {
        projectMetadataFiles.get()
            .asSequence()
            .map { it.asFile }
            .filter { it.exists() }
            .map { file ->
                async(Dispatchers.IO) { json.decodeFromStream<ProjectReleaseMetadata>(file.inputStream()) }
            }
            .toList()
            .awaitAll()
            .sorted()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeOutputJson(releaseMetadata: ReleaseMetadata) {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.outputStream().use { output ->
            json.encodeToStream<ReleaseMetadata>(releaseMetadata, output)
        }
    }
}
