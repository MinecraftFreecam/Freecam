import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import dev.kikugie.stonecutter.data.tree.ProjectNode
import io.github.z4kn4fein.semver.constraints.toMavenFormat
import net.xolt.freecam.model.ModMetadata
import org.gradle.api.Project
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

val Project.commonExpansions: Map<String, String>
    get() = mapOf(
        "mixinCompatLevel" to "JAVA_${meta.javaVersion}",
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
        "neoForgeVersion" to meta.deps["neoforge_version"],
        "neoforgeLoaderReq" to meta.reqs["neoforge_loader"]?.toMavenFormat(),
        "neoforgeReq" to meta.reqs["neoforge_version"]?.toMavenFormat(),
        "neoforgeMcReq" to meta.reqs["mc"]?.toMavenFormat(),
        "forgeVersion" to meta.deps["forge_version"],
        "forgeLoaderReq" to meta.reqs["forge_loader"]?.toMavenFormat(),
        "forgeReq" to meta.reqs["forge_version"]?.toMavenFormat(),
        "forgeMcReq" to meta.reqs["mc"]?.toMavenFormat(),
        "clothConfigReq" to meta.reqs["cloth"]?.toMavenFormat(),
    ).filterValues { it?.isNotEmpty() == true }.mapValues { it.value!! }
