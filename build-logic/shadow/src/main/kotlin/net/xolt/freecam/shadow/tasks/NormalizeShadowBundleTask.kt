package net.xolt.freecam.shadow.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject

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

        fs.sync {
            from(archives.zipTree(inputFile))
            into(dest)
        }
    }
}

/**
 * Get the name prefix of a jar file, stopping at the version number.
 * For example, `"cloth-config-5.3.63"` would return `"cloth-config"`.
 */
internal val String.namePrefix: String
    get() = split('-')
        .takeWhile { it.firstOrNull()?.isLetter() ?: true }
        .joinToString("-")
