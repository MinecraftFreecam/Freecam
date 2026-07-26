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
        val knownFields = setOf("curseforge_slug", "modrinth_id", "type")
        val errors = mutableListOf<String>()

        requireStonecutter("relationships")
            .properties.rawOrNull("relationships")
            ?.to<Map<String, Map<String, String>>>()
            ?.mapNotNull { (name, fields) ->
                (fields.keys - knownFields).takeUnless { it.isEmpty() }?.let { unknownFields ->
                    errors += "'relationship.$name' has unknown fields: " + unknownFields.joinToString(" ") { "'$it'" }
                }
                val curseforgeSlug = fields["curseforge_slug"].also {
                    if (it == null) errors += "'relationship.$name' is missing required field 'curseforge_slug'"
                }
                val modrinthId = fields["modrinth_id"].also {
                    if (it == null) errors += "'relationship.$name' is missing required field 'modrinth_id'"
                }
                val type = fields["type"]?.let { str ->
                    try {
                        Relationship.Type.valueOf(str.uppercase())
                    } catch (_: IllegalArgumentException) {
                        errors += "'relationship.$name' defines an invalid 'type' ('$str'), expected: " +
                            Relationship.Type.entries.joinToString(" ") { "'$it'"}
                        return@mapNotNull null
                    }
                }

                Relationship(
                    curseforgeSlug = curseforgeSlug ?: return@mapNotNull null,
                    modrinthId = modrinthId ?: return@mapNotNull null,
                    type = type ?: Relationship.Type.OPTIONAL,
                )
            }
            ?.also {
                require(errors.isEmpty()) {
                    "${project.path} has invalid relationship definitions:\n" + errors.joinToString("\n") { "- $it" }
                }
            }
            ?: emptyList()
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
