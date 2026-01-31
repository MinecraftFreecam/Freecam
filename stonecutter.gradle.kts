import org.jetbrains.changelog.tasks.BaseChangelogTask

// This file represents the version-less `rootProject` (:)
// Version-agnostic tasks should go here, as well as configuration for stonecutter itself

plugins {
    id("freecam.api")
    id("freecam.bump-version")
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.jetbrains.changelog)
}

stonecutter active "1.21.11"

stonecutter {
    parameters {
        val rootVersionProject = rootProject.project(node.metadata.project)
        val clothVersion = rootVersionProject.property("deps.cloth") as String
        dependencies["cloth"] = clothVersion
        rootVersionProject.findProperty("deps.neoforge")?.let {
            dependencies["neoforge"] = it as String
        }
        rootVersionProject.findProperty("deps.forge")?.let {
            dependencies["forge"] = it as String
        }

        replacements {
            string(current.parsed >= "1.21.11") {
                replace("ResourceLocation", "Identifier")
                replace("input.jumping", "input.keyPresses.jump()")
                replace("input.shiftKeyDown", "input.keyPresses.shift()")
                replace("input.up", "input.keyPresses.forward()")
                replace("input.down", "input.keyPresses.backward()")
                replace("input.right", "input.keyPresses.right()")
                replace("input.left", "input.keyPresses.left()")
            }
            string(current.parsed >= "1.20") {
                replace("canEnterPose(", "wouldNotSuffocateAtTargetPose(")
            }
            string(current.parsed >= "1.19") {
                replace("new net.minecraft.network.chat.TranslatableComponent(", "Component.translatable(")
                replace("new net.minecraft.network.chat.TextComponent(", "Component.literal(")
                replace("net.minecraft.network.chat.TextComponent.EMPTY", "Component.empty()")
            }
        }
    }
}

tasks.named<Wrapper>("wrapper") {
    // Use "all" so we get sources and javadoc too
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "9.2.1"
}

tasks.bumpVersion {
    input = file("gradle.properties")
    key = "mod.version"
}

// Move the changelog tasks to the "version" group
tasks.withType<BaseChangelogTask>().configureEach {
    group = "version"
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
