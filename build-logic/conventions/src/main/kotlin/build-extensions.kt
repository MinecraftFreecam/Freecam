import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import dev.kikugie.stonecutter.data.tree.ProjectNode
import net.xolt.freecam.model.ModMetadata
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType

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

val Project.requiredJava get() = when {
    stonecutter.current.parsed >= "26.0" -> JavaVersion.VERSION_25
    stonecutter.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
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
            "minecraftVersion" to meta.properties.orNull("minecraft_version"),
            "fabricLoaderVersion" to meta.deps.orNull("fabric_loader"),
            "fabricLoaderReq" to meta.properties.orNull("fabric_loader_req"),
            "fabricMcReq" to meta.properties.orNull("fabric_mc_req"),
            "fabricApiId" to if (stonecutter.current.parsed < "1.19.2") "fabric" else "fabric-api",
            "fabricApiVersion" to meta.deps.orNull("fabric_api"),
            "neoForgeVersion" to meta.deps.orNull("neoforge"),
            "neoforgeLoaderReq" to meta.properties.orNull("neoforge_loader_req"),
            "neoforgeReq" to meta.properties.orNull("neoforge_req"),
            "neoforgeMcReq" to meta.properties.orNull("neoforge_mc_req"),
            "forgeVersion" to meta.deps.orNull("forge"),
            "forgeLoaderReq" to meta.properties.orNull("forge_loader_req"),
            "forgeReq" to meta.properties.orNull("forge_req"),
            "forgeMcReq" to meta.properties.orNull("forge_mc_req"),
            "clothConfigReq" to meta.properties.orNull("cloth_config_req"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { it.value!! }
    }

// TODO: handle JSON as structured data, to avoid string injection hacks
val Project.commonJsonExpansions get() = buildMap {
    putAll(project.commonExpansions)
    mapValues { (_, v) -> v.replace("\n", "\\\\n") }
    put("modAuthorsJson", meta.authors.joinToString("\", \""))
}