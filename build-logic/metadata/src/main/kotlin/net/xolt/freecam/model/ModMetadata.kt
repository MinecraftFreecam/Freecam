package net.xolt.freecam.model

import dev.kikugie.stonecutter.AnyVersion
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import net.xolt.freecam.util.decodeTomlPath
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

internal infix fun StaticModMetadata.elaborate(project: Project): ModMetadata =
    ProjectModMetadata(project = project, meta = this)

private class ProjectModMetadata(
    private val project: Project,
    private val meta: StaticModMetadata,
)
: StaticModMetadata by meta, ModMetadata
{
    private val sc get() = project.extensions.findByType<StonecutterBuildExtension>()

    private fun requireStonecutter(property: String) =
        sc ?: error("${project.path} without `stonecutter` extension cannot read `$property`")

    override val mc: String
        get() = requireStonecutter("mc").current.version

    override val loader: String
        get() = requireStonecutter("loader").branch.id

    // Read `description` from the en_US i18n source file
    override val description: String by lazy {
        project
            .project(":i18n")
            .layout
            .projectDirectory
            .dir("src/main/en_US")
            .file("metadata.toml")
            .asFile
            .decodeTomlPath(id, "mod", "description")
    }

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
        requireStonecutter("supportedMinecraftVersions")
            .properties.rawOrNull("supported_mc_versions")?.to()
            ?: emptyList()
    }

    override val javaVersion: Int by lazy {
        requireStonecutter("javaVersion")
            .dependencies
            .getting("java")
            .map(AnyVersion::toInt)
            .get()
    }

    override val mod = PrefixedPropertyMap("mod.") { project.properties }
    override val deps = PrefixedPropertyMap("deps.") { project.properties }
}

private class PrefixedPropertyMap(
    private val prefix: String = "",
    private val properties: () -> Map<String, Any?>,
) : Map<String, String> {

    private val map by lazy {
        properties()
            .asSequence()
            .filter { (key, _) ->
                key.startsWith(prefix)
            }
            .mapNotNull { (key, value) ->
                (value as? String)?.let { key to it }
            }
            .associate { (key, value) ->
                key.removePrefix(prefix) to value
            }
    }

    override val size get() = map.size
    override val keys get() = map.keys
    override val values get() = map.values
    override val entries get() = map.entries
    override fun isEmpty() = map.isEmpty()
    override fun containsKey(key: String) = map.containsKey(key)
    override fun containsValue(value: String) = map.containsValue(value)
    override fun get(key: String) = map[key]
}
