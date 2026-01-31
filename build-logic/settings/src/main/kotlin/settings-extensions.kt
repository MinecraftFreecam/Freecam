import org.gradle.api.initialization.Settings
import org.slf4j.LoggerFactory.getLogger
import org.tomlj.Toml
import org.tomlj.TomlTable

private val logger = getLogger("stonecutter.settings")

/**
 * Loads `stonecutter.versions.toml` and returns a map of Gradle project names
 * to the Minecraft versions they should be built against.
 */
fun Settings.loadStonecutterVersions(): Map<String, List<String>> {
    val versionsFile = rootDir.resolve("stonecutter.versions.toml").toPath()

    logger.info("Loading stonecutter versions from {}", versionsFile)
    val table: TomlTable = Toml.parse(versionsFile)

    return table.keySet().fold(mutableMapOf<String, MutableSet<String>>()) { acc, version ->
        val projects = table.getTableOrEmpty(listOf(version))
            .getArrayOrEmpty("projects")
            .toList()
            .map { it.toString() }

        if (projects.isEmpty()) {
            // A configured version without any projects is an error, but avoid throwing as it could be done to
            // temporarily disable a build
            logger.error(
                "{} has no projects.\n  configured in: {}",
                version,
                versionsFile
            )
        }

        projects.forEach {
            acc.getOrPut(it) { mutableSetOf() }.add(version)
        }

        acc
    }.mapValues { (_, versions) -> versions.sorted() }
}
