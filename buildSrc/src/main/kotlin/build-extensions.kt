import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
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

val Project.common get() = requireNotNull(stonecutterBuild.node.sibling("common")) {
    "No common project for $project"
}
val Project.commonProject get() = rootProject.project(stonecutterBuild.current.project)
val Project.commonMod get() = commonProject.mod

val Project.commonExpansions: Map<String, String>
    get() {
        return mapOf(
            "javaVersion" to commonMod.propOrNull("java.version"),
            "modId" to commonMod.id,
            "modName" to commonMod.name,
            "modVersion" to commonMod.version,
            "modGroup" to commonMod.group,
            "modAuthors" to commonMod.authors.joinToString(", "),
            "modAuthorsJson" to commonMod.authors.joinToString("\", \""),
            "modDescription" to commonMod.description,
            "modLicense" to commonMod.license,
            "modHomepage" to commonMod.homepage,
            "modSource" to commonMod.source,
            "modIssues" to commonMod.issues,
            "modGhReleases" to commonMod.ghReleases,
            "modCurseforge" to commonMod.curseforge,
            "modModrinth" to commonMod.modrinth,
            "modCrowdin" to commonMod.crowdin,
            "minecraftVersion" to commonMod.propOrNull("minecraft_version"),
            "fabricLoaderVersion" to commonMod.depOrNull("fabric_loader"),
            "fabricLoaderReq" to commonMod.propOrNull("fabric_loader_req"),
            "fabricMcReq" to commonMod.propOrNull("fabric_mc_req"),
            "fabricApiVersion" to commonMod.depOrNull("fabric_api"),
            "neoForgeVersion" to commonMod.depOrNull("neoforge"),
            "neoforgeLoaderReq" to commonMod.propOrNull("neoforge_loader_req"),
            "neoforgeReq" to commonMod.propOrNull("neoforge_req"),
            "neoforgeMcReq" to commonMod.propOrNull("neoforge_mc_req"),
            "forgeVersion" to commonMod.depOrNull("forge"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { it.value!! }
    }
val Project.commonJsonExpansions get() =
    project.commonExpansions.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

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
    val mc: String get() = depOrNull("minecraft") ?: project.stonecutterBuild.current.version

    fun propOrNull(key: String) = project.prop(key)
    fun prop(key: String) = requireNotNull(propOrNull(key)) { "Missing '$key'" }
    fun modPropOrNull(key: String) = project.prop("mod.$key")
    fun modProp(key: String) = requireNotNull(modPropOrNull(key)) { "Missing 'mod.$key'" }
    fun depOrNull(key: String): String? = project.prop("deps.$key")?.takeIf { it.isNotEmpty() && it != "" }
    fun dep(key: String) = requireNotNull(depOrNull(key)) { "Missing 'deps.$key'" }
}