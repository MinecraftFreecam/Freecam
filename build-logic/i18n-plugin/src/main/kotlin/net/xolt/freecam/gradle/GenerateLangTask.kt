package net.xolt.freecam.gradle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import net.xolt.freecam.i18n.KeyTransformation
import net.xolt.freecam.i18n.LangDirConverter
import net.xolt.freecam.i18n.asMinecraftLocale
import net.xolt.freecam.i18n.isLocaleCode
import net.xolt.freecam.io.onEachConcurrent
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

@CacheableTask
abstract class GenerateLangTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: SetProperty<Directory>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Nested
    abstract val transformations: ListProperty<KeyTransformation>

    @TaskAction
    fun run(): Unit = runBlocking {
        val outputDir = outputDir.get().asFile
        sources.get().asFlow()
            .map { it.asFile }
            .onEach { src ->
                require(src.isDirectory) {
                    "Source must be a directory: ${src.absolutePath}"
                }
                require(src.name.isLocaleCode) {
                    "Source name '${src.name}' must be a locale code: ${src.absolutePath}"
                }
            }
            .onEachConcurrent { src ->
                LangDirConverter(
                    directory = src,
                    transformations = transformations.get().sortedBy(KeyTransformation::name),
                    destination = outputDir.resolve("${src.name.asMinecraftLocale}.json"),
                ).toLangJsonFile()
            }
            .flowOn(Dispatchers.IO)
            .collect()
    }
}
