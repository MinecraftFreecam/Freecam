package net.xolt.freecam.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.text.Regex.Companion.escape

/**
 * Bump a version property in a properties file
 */
abstract class BumpVersionTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @get:Input
    @get:Option(option = "major", description = "Increment the major version")
    val major = objects.property<Boolean>().convention(false)

    @get:Input
    @get:Option(option = "minor", description = "Increment the minor version")
    val minor = objects.property<Boolean>().convention(false)

    @get:Input
    @get:Option(option = "patch", description = "Increment the patch version")
    val patch = objects.property<Boolean>().convention(false)

    /**
     * The property key containing the version to bump
     */
    @get:Input
    val key = objects.property<String>().convention("version")

    /**
     * The `gradle.properties` file
     */
    @get:InputFile
    abstract val input: RegularFileProperty

    /**
     * Run by Gradle when executing implementing tasks.
     */
    @TaskAction
    fun run() {
        val file = input.asFile.get()
        logger.info("Updating ${file.path}")
        update(file)
    }

    private fun update(file: File) {
        // Regex matches `key` and captures both the value and any formatting
        val keyRegex = """^(\s*${escape(key.get())}\s*=\s*)(.*?)$""".toRegex()

        // Read all lines, replacing any that match the key regex
        val lines = file.readLines().map {
            it.replace(keyRegex) { match ->
                val left = match.groupValues[1]
                val v = match.groupValues[2]

                if (v.isEmpty()) {
                    logger.warn("Assuming empty version assigned to ${key.get()} is \"0\"")
                    left + bump("0")
                } else {
                    left + bump(v)
                }
            }
        }

        // Write the updated lines
        // FIXME trailing empty line at EOF is removed...
        file.writeText(lines.joinToString("\n"))
    }

    private fun bump(version: String): String {
        // Split the integer parts
        val v = version.split('.').map(String::toInt).toMutableList()

        logger.info("Bumping version \"$version\" with ${v.size} parts...")

        if (major.get()) {
            logger.info("- Bumping major version")
            v.bump(0)
        }

        if (minor.get()) {
            logger.info("- Bumping minor version")
            v.bump(1)
        }

        if (patch.get()) {
            logger.info("- Bumping patch version")
            v.bump(2)
        }

        // TODO support pre-release versions

        val new = v.joinToString(".")
        logger.info("- New version: $new")
        return new
    }

    // Increment the given index, adding new entries if necessary
    private fun MutableList<Int>.bump(index: Int) {
        val min = index + 1
        val max = max(3, min)

        // Ensure the version is long enough to bump this part
        while (size < min) {
            add(0)
        }

        // Do the thing!
        this[index]++

        // Cap version size (semver uses 3 parts)
        while (size > max) {
            removeLast()
        }

        // Reset lesser version parts to zero
        // E.g. reset `patch` when bumping `minor`
        for (i in min until size) {
            this[i] = 0
        }
    }
}
