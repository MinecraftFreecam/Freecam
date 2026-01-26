import org.gradle.api.initialization.Settings
import org.tomlj.Toml

/**
 * Loads `stonecutter.versions.toml` and returns a map of Gradle project names
 * to the Minecraft versions they should be built against.
 *
 * The `common` project is derived automatically from the versions used by all
 * other projects.
 */
fun Settings.loadStonecutterVersions(): Map<String, List<String>> {
    val versionsFile = rootDir.resolve("stonecutter.versions.toml").toPath()

    val table = Toml.parse(versionsFile)
        .getTable("projects")
        ?: error("Missing [projects] table")

        return buildMap {
            table.keySet().forEach { project ->
                table.getArrayOrEmpty(project)
                    .toList()
                    .takeUnless { it.isEmpty() }
                    ?.map { it.toString() }
                    ?.also { put(project, it) }
            }
            // compute :common as the union of all targeted versions
            values.flatten().distinct().also { allVersions ->
                put("common", allVersions)
            }
        }
    }
