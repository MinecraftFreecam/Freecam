import net.xolt.freecam.gradle.BumpVersionTask
import org.jetbrains.changelog.tasks.BaseChangelogTask

plugins {
    id("org.jetbrains.changelog")
}

if (this == rootProject) {
    tasks.named<Wrapper>("wrapper") {
        // Use "all" so we get sources and javadoc too
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "8.11"
    }

    // Helper task that bumps the version number
    tasks.register<BumpVersionTask>("bumpVersion") {
        group = "version"
        input = file("gradle.properties")
        key = "mod.version"
    }

    // Move the changelog tasks to the "version" group
    tasks.withType<BaseChangelogTask>().configureEach {
        group = "version"
    }

    // Don't need these on the root project
    afterEvaluate {
        tasks.named<Jar>("jar") { enabled = false }
        tasks.named<Jar>("sourcesJar") { enabled = false }
    }

    changelog {
        // Use the mod_version in gradle.properties as the release version/tag.
        // Build tag/diff links using the github repo URL.
        version = property("mod.version") as String
        repositoryUrl = property("mod.source") as String

        // Title & intro are printed right at the start of the changelog.
        // The values here will replace whatever exists in the file when patching.
        title = "Changelog"
        introduction = """
        All notable changes to this project will be documented in this file.
        
        This file is formatted as per [Keep a Changelog](https://keepachangelog.com/en/1.0.0),
        and Freecam's versioning is based on [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
        """.trimIndent()

        // Group sub-headings added to the "unreleased" section when patching the changelog.
        // Other sub-headings can still be added manually, if required.
        groups = listOf("Added",
            "Changed",
            "Removed",
            "Fixed")

        // Regex used to find versions in headings.
        // The default regex only supports semantic versions, this one is more lenient.
        headerParserRegex = Regex("(\\d+(?:\\.\\d+)+(?:-[-a-z]+\\d*)?)")
    }
}
