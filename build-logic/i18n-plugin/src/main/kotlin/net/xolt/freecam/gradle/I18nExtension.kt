package net.xolt.freecam.gradle

import net.xolt.freecam.i18n.isLocaleCode
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.setProperty
import javax.inject.Inject

private val logger = Logging.getLogger(I18nPlugin.EXTENSION_NAME)

abstract class I18nExtension @Inject constructor(
    private val layout: ProjectLayout,
    objects: ObjectFactory,
) {

    /**
     * Language-specific directory sources.
     *
     * The directory name should be the ISO locale of the specific language.
     *
     * Used as input for the [`generateLangFiles`][GenerateLangTask] task.
     */
    val sources: SetProperty<Directory> = objects.setProperty<Directory>().apply {
        val src = layout.projectDirectory.dir("src/main")
        val dirs = src.asFile.list { file, name -> file.isDirectory }
        val (valid, invalid) = dirs.partition { it.isLocaleCode }

        if (invalid.isNotEmpty()) logger.error(
            "Invalid locale directories in {}: {}",
            src.asFile.absolutePath,
            invalid.joinToString(", ")
        )

        convention(valid.map { src.dir(it) })
    }

    /**
     * Transformations for a language key.
     * Applied during the [`generateLangFiles`][GenerateLangTask] task.
     */
    val transformations: NamedDomainObjectContainer<KeyTransformationSpec> =
        objects.domainObjectContainer(KeyTransformationSpec::class.java)

    /**
     * The directory where language files are generated.
     */
    val generatedDir: DirectoryProperty = objects.directoryProperty()

    /**
     * Configure transformations for a language key.
     * Applied during the [`generateLangFiles`][GenerateLangTask] task.
     */
    fun transform(key: String, configure: Action<KeyTransformationSpec>) {
        transformations.register(key, configure)
    }
}
