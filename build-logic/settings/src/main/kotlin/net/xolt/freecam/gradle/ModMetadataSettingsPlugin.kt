package net.xolt.freecam.gradle

import dev.eav.tomlkt.Toml
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.xolt.freecam.model.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.findByType

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

    override val mc: String
        get() = requireNotNull(sc) {
            "${project.path} without `stonecutter` extension cannot read `mc` "
        }.current.version

    override val loader: String
        get() = requireNotNull(sc) {
            "${project.path} without `stonecutter` extension cannot read `loader` "
        }.branch.id

    override val relationships: List<Relationship> by lazy {
        project.properties
            .asSequence()
            .mapNotNull { (key, value) ->
                if (value is String) key to value else null
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

    override val supportedMinecraftVersions: List<String> by lazy {
        project.properties.getOrDefault("supported_mc_versions", null)
            ?.let { it as String }
            ?.splitToSequence(',')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.toList()
            ?: emptyList()
    }

    override fun parchment(block: (mappings: String, minecraft: String) -> Unit) {
        deps.orNull("parchment")
            ?.let(ParchmentVersion.Companion::parse)
            ?.let { block(it.mappings, it.minecraft ?: mc) }
    }

    override val properties = PrefixedPropertyProvider { project.properties }
    override val mod = PrefixedPropertyProvider("mod.") { project.properties }
    override val deps = PrefixedPropertyProvider("deps.") { project.properties }
}

private class PrefixedPropertyProvider(
    private val prefix: String = "",
    private val properties: () -> Map<String, Any?>,
) : PropertyProvider {
    override fun get(prop: String) = requireNotNull(orNull(prop)) { "Missing ${prefix + prop}" }
    override fun orNull(prop: String) = properties()[prefix + prop]?.toString()
    override fun asSequence() = properties()
        .asSequence()
        .filter { it.key.startsWith(prefix) && it.value != null }
        .map { it.key.removePrefix(prefix) to it.value.toString() }
}

class ModMetadataSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val name = "meta"

        // Load StaticModMetadata ourselves, rather than using stonecutter centralized properties
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
