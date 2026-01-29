package net.xolt.freecam.gradle

import dev.eav.tomlkt.Toml
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.ModMetadataToml
import net.xolt.freecam.model.ParchmentVersion
import net.xolt.freecam.model.PropertyProvider
import net.xolt.freecam.model.Relationship
import net.xolt.freecam.model.StaticModMetadata
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.findByType
import java.io.File
import java.util.Properties

@Serializable
private data class MetadataToml(
    val mod: ModMetadataToml,
)

private class ProjectModMetadata(
    private val project: Project,
    private val meta: StaticModMetadata,
)
: StaticModMetadata by meta, ModMetadata
{
    private val sc get() = project.extensions.findByType<StonecutterBuildExtension>()

    private val props by lazy {
        val sc = requireNotNull(this.sc) {
            "${project.path} without `stonecutter` does not have version-specific properties"
        }

        // Emulate Gradle's property cascade, but include version-specific properties too
        Properties().apply {
            sequenceOf(
                    // rootProject properties
                    project.rootDir.resolve("gradle.properties"),
                    // version project properties
                    project.rootDir.resolve("versions/${sc.current.version}/gradle.properties"),
                    // loader project properties
                    project.rootDir.resolve("${sc.branch.id}/gradle.properties"),
                    // project properties
                    project.projectDir.resolve("gradle.properties"),
                )
                .filter(File::exists)
                .map(File::inputStream)
                .forEach { it.use(::load) }
        }
    }

    override val mc: String
        get() = requireNotNull(sc) {
            "${project.path} without `stonecutter` extension cannot read `mc` "
        }.current.version

    override val loader: String
        get() = requireNotNull(sc) {
            "${project.path} without `stonecutter` extension cannot read `loader` "
        }.branch.id

    override val relationships: List<Relationship> by lazy {
        props
            .asSequence()
            .mapNotNull { (key, value) ->
                if (key is String && value is String) key to value else null
            }
            // Collect relationship properties
            .mapNotNull { (key, value) ->
                val path = key.split('.')
                if (path.size == 3 && path[0] == "relationship") path[1] to (path[2] to value)
                else null
            }
            // Group by relationship name
            .groupBy({ it.first }, { it.second })
            // Construct a Relationship object
            .map { (name, fields) ->
                val props = fields.toMap()
                val unknown = props.keys - setOf("curseforge_slug", "modrinth_id", "type")
                require(unknown.isEmpty()) {
                    "${project.path} unknown relationship fields: " + unknown.joinToString(" ") { "relationship.$name.$it" }
                }
                Relationship(
                    curseforgeSlug = props["curseforge_slug"]!!,
                    modrinthId = props["modrinth_id"]!!,
                    type = props["type"]
                        ?.let { Relationship.Type.valueOf(it.uppercase()) }
                        ?: Relationship.Type.OPTIONAL
                )
            }
            .toList()
    }

    override fun parchment(block: (mappings: String, minecraft: String) -> Unit) {
        deps.orNull("parchment")
            ?.let(ParchmentVersion.Companion::parse)
            ?.let { block(it.mappings, it.minecraft ?: mc) }
    }

    override val properties = PrefixedPropertyProvider { props }
    override val mod = PrefixedPropertyProvider("mod.") { props }
    override val deps = PrefixedPropertyProvider("deps.") { props }
}

private class PrefixedPropertyProvider(
    private val prefix: String = "",
    private val properties: () -> Properties,
) : PropertyProvider {
    override fun get(prop: String) = requireNotNull(orNull(prop)) { "Missing ${prefix + prop}" }
    override fun orNull(prop: String) = properties()[prefix + prop]?.toString()
}

class ModMetadataSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val name = "meta"
        val file = settings.rootDir.resolve("metadata.toml")
        val toml = file.readText()
        val metadata = Toml.decodeFromString<MetadataToml>(toml).mod

        settings.extensions.add<StaticModMetadata>(name, metadata)
        settings.gradle.settingsEvaluated {
            gradle.allprojects {
                extensions.add<ModMetadata>(name, ProjectModMetadata(this, metadata))
            }
        }
    }
}
