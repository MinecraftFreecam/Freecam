import org.gradle.api.initialization.Settings
import org.slf4j.LoggerFactory.getLogger
import org.tomlj.Toml
import org.tomlj.TomlTable

private val logger = getLogger("stonecutter.settings")

/**
 * Loads `stonecutter.versions.toml` and returns a map of Gradle project names
 * to the Minecraft versions they should be built against.
 *
 * The `common` project automatically includes the versions used by all other
 * projects.
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
            logger.warn(
                "{} has no projects; registering for :common only\n  configured in: {}",
                version,
                versionsFile
            )
        }

        // Implicitly add all versions to `:common`
        (projects + "common").forEach {
            acc.getOrPut(it) { mutableSetOf() }.add(version)
        }

        acc
    }.mapValues { (_, versions) -> versions.sorted() }
}
