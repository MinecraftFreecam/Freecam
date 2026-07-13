package net.xolt.freecam.shadow.tasks

import dev.eav.tomlkt.*
import net.xolt.freecam.model.FmlModsToml
import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject
import kotlin.io.path.Path

private val serializer = Toml { explicitNulls = false }
private val logger = Logging.getLogger(NormalizeShadowBundleTask::class.java)

@CacheableTask
abstract class NormalizeShadowBundleTask @Inject constructor(
    private val fs: FileSystemOperations,
    private val archives: ArchiveOperations,
    private val objects: ObjectFactory,
) : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputDirectory
    val outputDir = objects.directoryProperty().convention(project.layout.buildDirectory.dir("shadow-bundle"))

    @get:Internal
    val outputFiles = objects.fileCollection().from(
        inputFiles.elements.zip(outputDir) { files, rootDir ->
            files.map { rootDir.dir(it.asFile.nameWithoutExtension) }
        }
    )

    @TaskAction
    fun process() {
        val dest = outputDir.get().asFile.apply {
            deleteRecursively()
        }

        inputFiles.files.forEach { file ->
            file processInto dest.resolve(file.nameWithoutExtension)
        }
    }

    internal infix fun File.processInto(dest: File) {
        val inputFile = this
        val inputName = nameWithoutExtension.namePrefix

        val renamedIcons = mutableListOf<String>()

        fs.sync {
            from(archives.zipTree(inputFile))
            into(dest)

            filesMatching("icon.png") {
                renamedIcons += relativePath.pathString
                name = "$inputName-$name"
            }
        }

        dest.resolve("META-INF/mods.toml").takeIf(File::exists)?.let { file ->
            val modsToml = file
                .bufferedReader().use { reader ->
                    serializer.decodeFromNativeReader<TomlTable>(reader)
                }
                .normalizeModsToml { key, value ->
                    logger.error("$inputName: invalid top-level ${file.name} key '$key' with value '$value'")
                }
                .asFmlModsToml()
                .prefixLogoFiles(inputName, renamedIcons)

            file.bufferedWriter().use { writer ->
                serializer.encodeToNativeWriter(modsToml, writer)
            }
        }
    }
}

private fun TomlTable.asFmlModsToml() =
    serializer.decodeFromTomlElement<FmlModsToml>(this)

internal fun FmlModsToml.prefixLogoFiles(prefix: String, logoPaths: Iterable<String>) =
    logoPaths.fold(this) { acc, logo -> acc.prefixLogoFile(prefix, logo) }

internal fun TomlTable.normalizeModsToml(onInvalid: (String, TomlElement) -> Unit = { _, _ -> }) = TomlTable(
    toMutableMap().apply {
        // Cloth Config incorrectly defines some mod-level fields at the top-level
        sequenceOf("authors", "displayURL").forEach { key ->
            remove(key)?.also { onInvalid(key, it) }
        }
    }
)

internal fun FmlModsToml.prefixLogoFile(prefix: String, logo: String): FmlModsToml {
    fun String.prefixed() =
        if (this == logo) Path(this).let {
            it.resolveSibling("$prefix-${it.fileName}")
        }.toString()
        else this
    return copy(
        logoFile = logoFile?.prefixed(),
        mods = mods.map { mod ->
            mod.copy(logoFile = mod.logoFile?.prefixed())
        },
    )
}

/**
 * Get the name prefix of a jar file, stopping at the version number.
 * For example, `"cloth-config-5.3.63"` would return `"cloth-config"`.
 */
internal val String.namePrefix: String
    get() = split('-')
        .takeWhile { it.firstOrNull()?.isLetter() ?: true }
        .joinToString("-")
