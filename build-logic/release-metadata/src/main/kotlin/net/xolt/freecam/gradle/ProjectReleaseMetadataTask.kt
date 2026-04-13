package net.xolt.freecam.gradle

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.ProjectReleaseMetadata
import net.xolt.freecam.model.Relationship
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named

abstract class ProjectReleaseMetadataTask : DefaultTask() {

    private val json = Json { ignoreUnknownKeys = true }

    @get:Input
    abstract val displayName: Property<String>

    @get:Input
    abstract val loader: Property<String>

    @get:Input
    abstract val minecraft: Property<String>

    @get:Input
    abstract val supportedMinecraftVersions: ListProperty<String>

    @get:Input
    abstract val relationships: ListProperty<Relationship>

    @get:Input
    abstract val targetCompatibility: Property<JavaVersion>

    @get:Input
    abstract val artifactFileName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        val java = project.provider {
            project.extensions.getByType<JavaPluginExtension>()
        }
        val meta = project.provider {
            project.extensions.getByType<ModMetadata>()
        }
        displayName.convention(minecraft.flatMap { mc ->
            loader.flatMap { loader ->
                meta.map { meta ->
                    "${meta.version} for MC $mc ($loader)"
                }
            }
        })
        loader.convention(meta.map { it.loader })
        minecraft.convention(meta.map { it.mc })
        supportedMinecraftVersions.convention(meta.map { it.supportedMinecraftVersions })
        relationships.convention(meta.map { it.relationships })
        targetCompatibility.convention(java.map { it.targetCompatibility })
        artifactFileName.convention(
            project.tasks.named<Jar>("jar").flatMap { it.archiveFileName }
        )
        outputFile.convention(
            artifactFileName.flatMap { name ->
                val base = name.removeSuffix(".jar")
                project.layout.buildDirectory.file("metadata/$base.json")
            }
        )
    }

    @TaskAction
    @OptIn(ExperimentalSerializationApi::class)
    fun generate() {
        val mc = minecraft.get()

        val metadata = ProjectReleaseMetadata(
            displayName = displayName.get(),
            loader = loader.get(),
            minecraft = mc,
            filename = artifactFileName.get(),
            gameVersions = (supportedMinecraftVersions.get() + mc).sorted().distinct(),
            javaVersions = listOf(targetCompatibility.map(JavaVersion::toReleaseMetadataSlug).get()),
            relationships = relationships.get(),
        )

        outputFile.get().asFile.outputStream().use { output ->
            json.encodeToStream(metadata, output)
        }
    }
}
