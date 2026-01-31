import dev.eav.tomlkt.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.gradle.api.initialization.Settings
import org.slf4j.LoggerFactory.getLogger

private val logger = getLogger("stonecutter.settings")

@Serializable
private data class VersionEntry(val projects: List<String> = emptyList())

private typealias VersionsToml = Map<String, VersionEntry>

private fun VersionsToml.toProjectVersionsMap(): Map<String, List<String>> =
    entries.fold(
        initial = mutableMapOf<String, MutableSet<String>>()
    ) { acc, (version, entry) ->
        val (projects) = entry

        if (projects.isEmpty()) {
            // A configured version without any projects is an error, but avoid throwing as it could be done to
            // temporarily disable a build
            logger.error("{} has no projects.", version)
        }

        projects.forEach {
            acc.getOrPut(it) { mutableSetOf() }.add(version)
        }

        acc
    }.mapValues { (_, versions) -> versions.sorted() }


/**
 * Loads `stonecutter.versions.toml` and returns a map of Gradle project names
 * to the Minecraft versions they should be built against.
 */
fun Settings.loadStonecutterVersions(): Map<String, List<String>> {
    val text = rootDir.resolve("stonecutter.versions.toml").readText()
    return Toml.decodeFromString<VersionsToml>(text).toProjectVersionsMap()
}
