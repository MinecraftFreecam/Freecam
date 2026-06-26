package net.xolt.freecam.model

import dev.kikugie.stonecutter.AnyVersion
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.github.z4kn4fein.semver.constraints.ConstraintFormatException
import io.github.z4kn4fein.semver.constraints.toConstraint
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
            .mapNotNull { (name, fields) ->
                val props = fields.toMap()
                val type = props["type"]
                    ?.uppercase()
                    ?.also { if (it == "NONE") return@mapNotNull null }
                    ?.let(Relationship.Type::valueOf)
                    ?: Relationship.Type.OPTIONAL

                val unknown = props.keys - setOf("curseforge_slug", "modrinth_id", "type")
                require(unknown.isEmpty()) {
                    "${project.path} unknown relationship fields: " + unknown.joinToString(" ") { "relationship.$name.$it" }
                }

                Relationship(
                    type = type,
                    curseforgeSlug = props["curseforge_slug"]!!,
                    modrinthId = props["modrinth_id"]!!,
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

    override val mod by lazy { project.properties.toPrefixMap("mod.") }
    override val deps by lazy { project.properties.toPrefixMap("deps.") }
    override val reqs by lazy {
        project.properties.toPrefixMap("reqs.").mapValues { (key, value) ->
            try {
                value.toConstraint()
            } catch (e: ConstraintFormatException) {
                error("${project.path} reqs.$key='$value': ${e.message}")
            }
        }
    }
}

private fun Map<String, Any?>.toPrefixMap(prefix: String) =
    asSequence()
        .filter { (key, _) ->
            key.startsWith(prefix)
        }
        .mapNotNull { (key, value) ->
            (value as? String)?.let { key to it }
        }
        .associate { (key, value) ->
            key.removePrefix(prefix) to value
        }
