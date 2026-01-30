import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import dev.kikugie.stonecutter.data.tree.ProjectNode
import net.xolt.freecam.model.ParchmentVersion
import net.xolt.freecam.model.Relationship
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

val Project.stonecutterBuild get() = extensions.getByType<StonecutterBuildExtension>()
val Project.stonecutterController get() = extensions.getByType<StonecutterControllerExtension>()

/**
 * The stonecutter [ProjectNode] for the current version's `:common` project, e.g. `:common:1.12.11`.
 */
val Project.commonNode: ProjectNode get() = requireNotNull(stonecutterBuild.node.sibling("common")) {
    "No common project for $project"
}

/**
 * The current version's `rootProject`, e.g. `project(":1.21.11")`.
 */
val Project.currentRootProject get() = rootProject.project(stonecutterBuild.current.project)!!

/**
 * [Project.mod] for the [current version's root project][Project.currentRootProject].
 */
val Project.currentMod get() = currentRootProject.mod

val Project.requiredJava get() = when {
    stonecutterBuild.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    stonecutterBuild.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    stonecutterBuild.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}
val Project.javaVersion get() = requiredJava.majorVersion.toInt()
val Project.javaLanguageVersion get() = JavaLanguageVersion.of(javaVersion)

val Project.commonExpansions: Map<String, String>
    get() {
        return mapOf(
            "mixinCompatLevel" to "JAVA_$javaVersion",
            "modId" to currentMod.id,
            "modName" to currentMod.name,
            "modVersion" to currentMod.version,
            "modGroup" to currentMod.group,
            "modAuthors" to currentMod.authors.joinToString(", "),
            "modDescription" to currentMod.description,
            "modLicense" to currentMod.license,
            "modHomepage" to currentMod.homepage,
            "modSource" to currentMod.source,
            "modIssues" to currentMod.issues,
            "modGhReleases" to currentMod.ghReleases,
            "modCurseforge" to currentMod.curseforge,
            "modModrinth" to currentMod.modrinth,
            "modCrowdin" to currentMod.crowdin,
            "minecraftVersion" to currentMod.propOrNull("minecraft_version"),
            "fabricLoaderVersion" to currentMod.depOrNull("fabric_loader"),
            "fabricLoaderReq" to currentMod.propOrNull("fabric_loader_req"),
            "fabricMcReq" to currentMod.propOrNull("fabric_mc_req"),
            "fabricApiVersion" to currentMod.depOrNull("fabric_api"),
            "neoForgeVersion" to currentMod.depOrNull("neoforge"),
            "neoforgeLoaderReq" to currentMod.propOrNull("neoforge_loader_req"),
            "neoforgeReq" to currentMod.propOrNull("neoforge_req"),
            "neoforgeMcReq" to currentMod.propOrNull("neoforge_mc_req"),
            "forgeVersion" to currentMod.depOrNull("forge"),
            "forgeLoaderReq" to currentMod.propOrNull("forge_loader_req"),
            "forgeReq" to currentMod.propOrNull("forge_req"),
            "forgeMcReq" to currentMod.propOrNull("forge_mc_req"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { it.value!! }
    }

// TODO: handle JSON as structured data, to avoid string injection hacks
val Project.commonJsonExpansions get() = buildMap {
    putAll(project.commonExpansions)
    mapValues { (_, v) -> v.replace("\n", "\\\\n") }
    put("modAuthorsJson", currentMod.authors.joinToString("\", \""))
}

val Project.loader: String? get() = prop("loader")

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = modProp("id")
    val name: String get() = modProp("name")
    val version: String get() = modProp("version")
    val releaseType: String get() = modProp("release_type")
    val group: String get() = modProp("group")
    val authors: List<String> get() =
        modProp("authors")
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
    val description: String get() = modProp("description")
    val license: String get() = modProp("license")
    val homepage: String get() = modProp("homepage")
    val source: String get() = modProp("source")
    val issues: String get() = modProp("issues")
    val ghReleases: String get() = modProp("gh_releases")
    val curseforge: String get() = modProp("curseforge")
    val modrinth: String get() = modProp("modrinth")
    val crowdin: String get() = modProp("crowdin")
    val relationships: List<Relationship> get() =
        project.properties
            .asSequence()
            // Collect relationship properties
            .mapNotNull { (key, value) ->
                val path = key.split('.')
                if (path.size == 3 && path[0] == "relationship") path[1] to (path[2] to value.toString())
                else null
            }
            // Group by relationship name
            .groupBy({ it.first }, { it.second })
            // Construct a Relationship object
            .map { (name, fields) ->
                val props = fields.toMap()
                val unknown = props.keys - setOf("curseforge_slug", "modrinth_id", "type")
                if (unknown.isNotEmpty()) {
                    error("Unknown relationship fields: " + unknown.joinToString(" ") { "relationship.$name.$it" })
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
    val parchment: ParchmentVersion? get() = depOrNull("parchment")?.let(ParchmentVersion::parse)
    val mc: String get() = depOrNull("minecraft") ?: project.stonecutterBuild.current.version

    inline fun parchment(block: (mappings: String, minecraft: String) -> Unit) {
        parchment?.let { block(it.mappings, it.minecraft ?: mc) }
    }

    fun propOrNull(key: String) = project.prop(key)
    fun prop(key: String) = requireNotNull(propOrNull(key)) { "Missing '$key'" }
    fun modPropOrNull(key: String) = project.prop("mod.$key")
    fun modProp(key: String) = requireNotNull(modPropOrNull(key)) { "Missing 'mod.$key'" }
    fun depOrNull(key: String): String? = project.prop("deps.$key")?.takeIf { it.isNotEmpty() && it != "" }
    fun dep(key: String) = requireNotNull(depOrNull(key)) { "Missing 'deps.$key'" }
}