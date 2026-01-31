import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import dev.kikugie.stonecutter.data.tree.ProjectNode
import net.xolt.freecam.model.ModMetadata
import net.xolt.freecam.model.ParchmentVersion
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

/**
 * Alias for [`meta`][ModMetadata], as seen outside build-logic.
 */
internal val Project.meta get() = extensions.getByType<ModMetadata>()

/**
 * The `stonecutter` extension on :loader:version projects is a [StonecutterBuildExtension].
 * This binding provides access within build-logic.
 */
internal val Project.stonecutter get() = extensions.getByType<StonecutterBuildExtension>()

/**
 * The `stonecutter` extension in `stonecutter.gradle.kts` is a [StonecutterControllerExtension].
 * This binding provides access within build-logic.
 */
internal val Project.stonecutterController get() = extensions.getByType<StonecutterControllerExtension>()

/**
 * The stonecutter [ProjectNode] for the current version's `:common` project, e.g. `:common:1.12.11`.
 */
val Project.commonNode: ProjectNode get() = requireNotNull(stonecutter.node.sibling("common")) {
    "No common project for $project"
}

/**
 * The current version's `rootProject`, e.g. `project(":1.21.11")`.
 */
val Project.currentRootProject get() = rootProject.project(stonecutter.current.project)!!

/**
 * [Project.mod] for the [current version's root project][Project.currentRootProject].
 */
val Project.currentMod get() = currentRootProject.mod

val Project.requiredJava get() = when {
    stonecutter.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    stonecutter.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    stonecutter.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}
val Project.javaVersion get() = requiredJava.majorVersion.toInt()
val Project.javaLanguageVersion get() = JavaLanguageVersion.of(javaVersion)

val Project.commonExpansions: Map<String, String>
    get() {
        return mapOf(
            "mixinCompatLevel" to "JAVA_$javaVersion",
            "modId" to meta.id,
            "modName" to meta.name,
            "modVersion" to meta.version,
            "modGroup" to meta.group,
            "modAuthors" to meta.authors.joinToString(", "),
            "modDescription" to meta.description,
            "modLicense" to meta.license,
            "modHomepage" to meta.homepageUrl.toString(),
            "modSource" to meta.sourceUrl.toString(),
            "modIssues" to meta.issuesUrl.toString(),
            "modGhReleases" to meta.githubReleasesUrl.toString(),
            "modCurseforge" to meta.curseforgeUrl.toString(),
            "modModrinth" to meta.modrinthUrl.toString(),
            "modCrowdin" to meta.crowdinUrl.toString(),
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
    put("modAuthorsJson", meta.authors.joinToString("\", \""))
}

val Project.loader: String? get() = prop("loader")

@JvmInline
value class ModData(private val project: Project) {
    val parchment: ParchmentVersion? get() = depOrNull("parchment")?.let(ParchmentVersion.Companion::parse)
    val mc: String get() = depOrNull("minecraft") ?: project.stonecutter.current.version

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