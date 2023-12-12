package freecam

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.SourceSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BuildConfigPlugin implements Plugin<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(BuildConfigPlugin)

    static interface Extension {
        MapProperty<String, Object> getConfig();
    }

    @Override
    void apply(Project project) {
        // TODO clean output dir before building
        // TODO run task on idea sync and/or afterEval? Avoid needing to do a build to update BuildConfig.
        // TODO move the extension config into the task?
        def extension = project.extensions.create('configureBuildConfig', Extension)

        Map<String, Object> baseConfig = [
                modid: project.rootProject.name.toLowerCase(),
                name: project.rootProject.name.capitalize(),
                version: project.version,
                mcVersion: project.rootProject.minecraft_version,
        ]

        def buildConfigTask = project.tasks.register("buildConfig", GenerateSource) {
            // We could list all the input properties, but the task is pretty cheap...
            // Keep things simple for now
            outputs.upToDateWhen {false}

            outputDir = project.layout.buildDirectory.dir "build-config"
            className = "BuildConfig"
            data.putAll baseConfig
            data.putAll extension.config.getOrElse(Collections.emptyMap())
            formatFieldNames = true
        }

        project.afterEvaluate {
            project.sourceSets.matching { it.name == 'main' }.each { SourceSet set ->
                LOG.warn("Added buildConfig to main source set on ${project.path}")
                set.java.srcDir project.tasks.buildConfig
            }
        }

    }
}
