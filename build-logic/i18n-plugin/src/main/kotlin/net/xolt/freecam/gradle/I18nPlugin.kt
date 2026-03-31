package net.xolt.freecam.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register


abstract class I18nPlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "i18n"
        const val LANG_ELEMENTS_NAME = "langElements"
        const val GENERATE_LANG_TASK_NAME = "generateLang"
    }

    override fun apply(project: Project): Unit = with(project) {
        val extension = extensions.create<I18nExtension>(EXTENSION_NAME, layout)

        val generate = tasks.register<GenerateLangTask>(GENERATE_LANG_TASK_NAME) {
            group = "build"
            description = "Process the i18n sources into language files"
            sources.set(extension.sources)
            transformations.set(provider {
                extension.transformations
                    .map { it.asKeyTransformation }
                    .sortedBy { it.name }
                    .toList()
            })
            outputDir.convention(layout.buildDirectory.dir("lang"))
        }
        extension.generatedDir.set(generate.flatMap { it.outputDir })

        configurations.create(LANG_ELEMENTS_NAME) {
            description = "The processed i18n language files"
            isCanBeConsumed = true
            isCanBeResolved = false

            attributes {
                attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "directory")
            }

            outgoing.artifact(generate)
        }
    }
}
