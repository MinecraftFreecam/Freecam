import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.xolt.freecam.shadow.tasks.NormalizeShadowBundleTask

/**
 * Applying this plugin to a project will transitively pull in its classpath,
 * giving the project access to `:build-logic:shadow` classes.
 *
 * It also applies the GradleUp `shadow` plugin and configures it to use the
 * `bundle` configuration.
 */

plugins {
    id("com.gradleup.shadow")
}

/**
 * Include a dependency in the `shadowJar` task.
 *
 * We avoid using the default `shadow` configuration, because it is pre-configured to inherit from other configurations.
 */
val bundle = configurations.create("bundle") {
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
}

val normalizeShadowBundleTask = tasks.register<NormalizeShadowBundleTask>("normalizeShadowBundle") {
    description = "Normalize dependencies before bundling with Shadow"
    inputFiles.from(bundle)
}

tasks.named<Jar>("jar") {
    archiveClassifier = "minimal"
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier = null

    // Don't use the `shadow` configuration
    configurations = emptySet()
    from(normalizeShadowBundleTask.map { it.outputFiles })

    duplicatesStrategy = DuplicatesStrategy.FAIL
    failOnDuplicateEntries = true
}

configurations {
    named("apiElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(shadowJar)
    }
    named("runtimeElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(shadowJar)
    }
}
