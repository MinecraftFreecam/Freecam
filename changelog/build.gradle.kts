import dev.eav.tomlkt.*
import org.jetbrains.changelog.tasks.GetChangelogTask

plugins {
    alias(libs.plugins.jetbrains.changelog)
}

buildscript {
    dependencies.classpath(libs.kotlin.serialization.toml)
}

/** The actual changelog file lives outside this build, in the repo root. */
val src = rootDir.resolveSibling("CHANGELOG.md")

/**
 * To minimize dependencies, we don't have `ModMetadata` or `freecam.metadata` here.
 * For now, just load it manually as a raw [TomlTable].
 */
val meta by lazy {
    logger.lifecycle("[changelog] Loading mod metadata")
    rootDir.resolveSibling("metadata.toml").bufferedReader().use { reader ->
        Toml.decodeFromReader<TomlTable>(TomlNativeReader(reader))
    }.getTable("mod")
}

changelog {
    path = src.canonicalPath

    // Use metadata.toml's mod version
    version = provider { meta.getString("version") }

    // Use metadata.toml's GitHub URL to form tag & diff links
    repositoryUrl = provider { meta.getString("source") }

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
    groups = listOf(
        "Added",
        "Changed",
        "Removed",
        "Fixed",
    )

    // Regex used to find versions in headings.
    // The default regex only supports semantic versions, this one is more lenient.
    headerParserRegex = "(\\d+(?:\\.\\d+)+(?:-[-a-z]+(?:\\.\\d+)?)?)".toRegex()
}

val getReleaseNotes by tasks.registering(GetChangelogTask::class) {
    group = "changelog"
    description = "Builds the current release notes"
    changelog = project.changelog.instance

    noHeader = true
    noLinks = true
    noEmptySections = true
    noSummary = false
    unreleased = false

    outputFile = project.changelog.version.flatMap { version ->
        project.layout.buildDirectory.file("build/$version/changelog.md")
    }
}

val releaseNotes by configurations.registering {
    isCanBeConsumed = true
    isCanBeResolved = false

    outgoing.artifacts(getReleaseNotes.map { it.outputs.files }) {
        builtBy(getReleaseNotes)
    }
}
