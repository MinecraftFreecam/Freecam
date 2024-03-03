package net.xolt.freecam.gradle

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.xolt.freecam.extensions.childDirectories
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * A Gradle task that builds translations into [variant]-specific lang files, compatible with minecraft.
 */
abstract class LangTask : DefaultTask() {

    /**
     * The directory where language files should be loaded from.
     */
    @get:InputDirectory
    abstract val inputDirectory: DirectoryProperty

    /**
     * The directory where language files should be written to.
     */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    /**
     * The "build variant" of the language files to target.
     *
     * E.g. `"normal"` or `"modrinth"`.
     *
     * @sample "normal"
     * @sample "modrinth"
     */
    @get:Input
    abstract val variant: Property<String>

    /**
     * The "source" language that translations are based on.
     *
     * Defaults to `"en-US"`.
     */
    @get:Input
    abstract val source: Property<String>

    /**
     * The mod ID.
     *
     * Used in the output file structure as well as some translation keys.
     */
    @get:Input
    abstract val modId: Property<String>

    private val json = Json { prettyPrint = true }
    private val localeRegex = "^[a-z]{2}-[A-Z]{2}$".toRegex()
    private val processors = listOf(
        VariantTooltipProcessor(),
        ModDescriptionProcessor(),
        ModNameProcessor()
    )

    init {
        @Suppress("LeakingThis")
        source.convention("en-US")
    }

    /**
     * Run by Gradle when executing implementing tasks.
     */
    @TaskAction
    fun build() {
        val languages = inputDirectory.get().asFile
            .childDirectories()
            .filter { it.name.matches(localeRegex) }
            .associate { it.name to readLangDir(it) }

        val base = languages[source.get()]

        languages.forEach { (lang, translations) ->
            writeJsonFile(fileFor(lang), processLang(translations, base).toSortedMap())
        }
    }

    /**
     * Get the given translation, for the given language.
     *
     * Will fall back to using the [source language][source] if the key isn't
     * found in the specified language or if language isn't specified.
     *
     * Should only be used **after** this task has finished executing.
     * I.e. **not** during Gradle's configuration step.
     *
     * @param key the translation key
     * @param lang the locale, e.g. en-US, en_us, or zh-CN
     * @return the translation, or null if not found
     */
    @JvmOverloads
    fun getTranslation(key: String, lang: String = source.get()): String? {
        val file = fileFor(lang)
        val translation = readJsonFile(file)[key]

        // Check "source" translation if key wasn't found
        return if (translation == null && file != fileFor(source.get())) {
            getTranslation(key)
        } else {
            translation
        }
    }

    private fun fileFor(lang: String) = outputDirectory.get().asFile
        .resolve("assets")
        .resolve(modId.get())
        .resolve("lang")
        .resolve(normaliseMCLangCode(lang) + ".json")

    // NOTE: Some lang codes may need manual mapping...
    // I couldn't find any examples though, so it's unlikely to affect us
    private fun normaliseMCLangCode(lang: String) = lang.lowercase().replace('-', '_')

    // Applies all processors to the given translations.
    // Does not use fallback to add missing translations, that is done in-game by MC
    // Some processors may use fallback to fill in missing _parts_ of translations though.
    private fun processLang(translations: Map<String, String>, fallback: Map<String, String>?) =
        processors.fold(translations) { acc, processor ->
            processor.process(modId.get(), variant.get().lowercase(), acc, fallback)
        }

    // Read and combine translation files in dir
    private fun readLangDir(dir: File) = dir
        .listFiles { _, name -> name.endsWith(".json") }
        ?.map { readJsonFile(it) }
        ?.flatMap { it.entries }
        ?.associate { it.toPair() }
        ?: emptyMap()

    @OptIn(ExperimentalSerializationApi::class)
    private fun readJsonFile(file: File): Map<String, String> = json.decodeFromStream(file.inputStream())

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeJsonFile(file: File, translations: Map<String, String>) {
        file.parentFile.mkdirs()
        file.createNewFile()
        json.encodeToStream(translations, file.outputStream())
    }
}
