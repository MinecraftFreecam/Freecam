package freecam

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.language.jvm.tasks.ProcessResources

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING
import static org.gradle.api.logging.LogLevel.WARN

/**
 * OverridePlugin ({@code freecam.override}) is used to apply targeted changes during the {@link ProcessResources} task.
 * <p>
 * In particular, it supports injecting new values into JSON files without having to replace the entire file.
 * In the future this could be extended to better support other file types.
 * <p>
 * Currently, non-JSON override files will simply replace the base file.
 */
class OverridePlugin implements Plugin<Project> {

    static interface Extension {
        Property<File> getOverrideDir()
    }

    @Override
    void apply(Project project) {
        // resourceOverrides.overrideDir
        def extension = project.extensions.create('resourceOverrides', Extension)
        extension.overrideDir.convention(project.file("src/resourceOverrides"))

        // Extend the ProcessResources tasks, overriding their output
        project.tasks.withType(ProcessResources).configureEach {
            // ProcessResources task is out-of-date if our override files change
            inputs.dir extension.overrideDir.get()

            def outPath = Paths.get outputs.files.asPath
            def overrideFiles = project.fileTree extension.overrideDir.get()
            def jsonFiles = project.fileTree dir: outPath, include: "**/*.json"

            // Handle overrides after processing resources so that we can operate on "finished" files
            doLast {
                overrideFiles.each { File file ->
                    def relPath = extension.overrideDir.get().relativePath file
                    def existing = outPath.resolve relPath
                    if (jsonFiles.contains existing.toFile()) {
                        // Include json overrides
                        logger.log WARN, "File has JSON override: ${relPath}"
                        overrideJson(existing, file.toPath())
                    } else {
                        // Include unrecognised overrides
                        if (Files.exists(existing)) {
                            logger.log WARN, "Override will completely overwrite ${relPath}"
                        } else {
                            logger.log WARN, "Override doesn't override anything: ${relPath}"
                        }
                        Files.copy file.toPath(), existing, REPLACE_EXISTING
                    }
                }
            }
        }
    }

    /**
     * Override a JSON file
     */
    static void overrideJson(Path base, Path override) {
        def baseJson = new JsonSlurper().parse(base)
        def overrideJson = new JsonSlurper().parse(override)
        def merged = recursiveMergeJson(baseJson, overrideJson)
        base.text = JsonOutput.prettyPrint(JsonOutput.toJson(merged))
    }

    /**
     * Recursively merge overrides into a JSON object.
     * <p>
     * If {@code base} and {@code override} share a common key,
     * and both values are JSON Objects (dictionaries/maps),
     * then those values will be merged recursively.
     *
     * Otherwise, {@code override}'s value will be used and
     * {@code base}'s will be discarded.
     *
     * @return A copy of base, with override's values merged in.
     */
    private static Object recursiveMergeJson(base, override) {
        // Merge map entries recursively
        if (base instanceof Map && override instanceof Map) {
            // Clone baseJson to avoid side effects
            // Use a TreeMap to preserve ordering
            def merged = new TreeMap(base)
            override.forEach { key, overrideEntry ->
                merged.compute(key) { _, baseEntry ->
                    baseEntry == null ? overrideEntry : recursiveMergeJson(baseEntry, overrideEntry)
                }
            }
            return merged
        }
        // Just replace lists, primitives & null entries
        return override
    }
}
