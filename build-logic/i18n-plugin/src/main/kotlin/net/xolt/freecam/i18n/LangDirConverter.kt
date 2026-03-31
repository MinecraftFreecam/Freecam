package net.xolt.freecam.i18n

import dev.eav.tomlkt.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.gradle.I18nPlugin
import net.xolt.freecam.io.concurrentMap
import net.xolt.freecam.util.mergeWith
import org.gradle.api.logging.Logging
import org.slf4j.Logger
import java.io.File

class LangDirConverter(
    private val directory: File,
    private val transformations: List<KeyTransformation>,
    private val destination: File,
    private val logger: Logger = Logging.getLogger(I18nPlugin.EXTENSION_NAME),
    private val json: Json = Json { prettyPrint = true },
) {
    val locale: String = directory.name

    /**
     * Reads a directory and converts its TOML files into a single `lang.json` file.
     *
     * The output file is not written if it would be empty.
     */
    suspend fun toLangJsonFile() {
        directory.listFiles { it.isTomlFile }
            .asFlow()
            .flowOn(Dispatchers.IO)
            .concurrentMap { file ->
                file to withErrorContext({ "[$locale] while flattening ${file.name}" }) {
                    file.decodeFlatToml()
                }
            }
            .fold(emptyMap<String, String>()) { acc, (file, values) ->
                withErrorContext({ "[$locale] while merging file ${file.name}" }) {
                    acc mergeWith values
                }
            }
            .let {
                withErrorContext({ "[$locale] while transforming keys" }) {
                    it.applyTransformations()
                }
            }
            .takeIf { it.isNotEmpty() }
            ?.toSortedMap()
            // SortedMap is not @Serializable
            ?.let { it as Map<String, String> }
            ?.writeJsonOutput()
    }

    internal fun File.decodeFlatToml(): Map<String, String> = flatten(original = decodeToml())

    private fun flatten(prefix: String = "", original: TomlTable): Map<String, String> = buildMap {
        original.forEach { (key, value) ->
            val fullKey = if (prefix.isEmpty()) key else "$prefix.$key"

            if (value is TomlTable) {
                this += flatten(prefix = fullKey, original = value)
            } else try {
                this[fullKey] = Toml.decodeFromTomlElement<String>(value)
            } catch (_: Exception) {
                error("Unsupported TOML value at $fullKey: $value")
            }
        }
    }

    internal fun Map<String, String>.applyTransformations() =
        applyMapTransformations(this)

    private fun applyMapTransformations(original: Map<String, String>) = buildMap {
        // Start with the original values
        putAll(original)

        transformations.groupBy { it.name }.forEach { (key, transformations) ->

            val keepOriginal = transformations.asSequence().map { it.keepOriginal }.distinct().singleOrNull()

            // Validate transformations keepOriginal behavior is consistent for each key
            check(keepOriginal != null) {
                val conflicts = transformations.joinToString(
                    separator = ", ",
                    truncated = "…",
                    limit = 4,
                    transform = {
                        val names = it.names.joinToString(
                            prefix = "[",
                            separator = ", ",
                            truncated = "…",
                            postfix = "]",
                            limit = 4,
                        ) { name -> "'$name'" }
                        "$key(names=$names, keepOriginal=${it.keepOriginal})"
                    },
                )
                "Transformations for '$key' have conflicting 'keepOriginal' values: $conflicts"
            }

            val (alreadyDefined, renames) = transformations.asSequence()
                .flatMap { it.names.asSequence() }
                .distinct()
                .partition { containsKey(it) }

            // Ensure the target renames haven't already been defined in the resulting map
            // Conflicts from another transformation are tagged with '*'
            check(alreadyDefined.isEmpty()) {
                val conflicts = alreadyDefined.joinToString(
                    separator = ", ",
                    truncated = "…",
                    limit = 4,
                ) { name ->
                    val tag = original[name]?.let { "" } ?: "*"
                    "$tag'$name'"
                }
                "Transformations for '$key' specify renames that already exist: $conflicts"
            }

            // Verify each transformation defined some action
            if (keepOriginal && transformations.any { it.names.isEmpty() }) {
                logger.warn("[$locale] Transformation for '$key' does not define any transformation")
            }

            val value = original.getOrElse(key) {
                // Can't transform something that doesn't exist
                logger.warn("[$locale] Missing key '$key' with transformation")
                return@forEach
            }

            // Apply renames, if configured
            if (renames.isNotEmpty()) {
                val targets = renames.joinToString(
                    separator = ", ",
                    truncated = "…",
                    limit = 4,
                ) { name -> "'$name'" }
                logger.debug("[$locale] renaming key '$key' to: $targets")
                renames.forEach { name -> this[name] = value }
            }

            // Unless keeping, remove the original
            if (!keepOriginal) {
                logger.debug("[$locale] removing original key '$key'")
                remove(key)
            }
        }
    }

    private inline fun <reified T> File.decodeToml(): T =
        bufferedReader().use { reader ->
            try {
                Toml.decodeFromReader(TomlNativeReader(reader))
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> T.writeJsonOutput() {
        destination.outputStream().use { output ->
            json.encodeToStream(this, output)
        }
    }

    private val File.isTomlFile: Boolean
        get() = isFile && extension == "toml"
}