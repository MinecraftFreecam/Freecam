import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.tasks.BaseChangelogTask

// This file represents the version-less `rootProject` (:)
// Version-agnostic tasks should go here, as well as configuration for stonecutter itself

plugins {
    id("freecam.api")
    id("freecam.release-metadata")
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.jetbrains.changelog)
}

stonecutter active "26.1"

stonecutter parameters {
    // Tags that stonecutter will associate with this project when parsing TOML properties
    // See https://stonecutter.kikugie.dev/wiki/config/properties#property-tags
    //
    // branch id (common, fabric, …) metadata version (actual mc version), project name (approx mc version)
    properties.tags(node.branch.id, node.metadata.version, node.project.name)

    val meta = node.project.meta

    // Register project dependencies with stonecutter
    meta.deps.asSequence()
        .filter { (key, value) ->
            // Parchment has an incompatible version syntax
            key != "parchment" && value.isNotBlank()
        }
        .forEach { (key, value) ->
            dependencies[key] = value
        }

    dependencies["java"] = when {
        current.parsed >= "26.0" -> JavaVersion.VERSION_25
        current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
        current.parsed >= "1.18" -> JavaVersion.VERSION_17
        current.parsed >= "1.17" -> JavaVersion.VERSION_16
        else -> JavaVersion.VERSION_1_8
    }.majorVersion

    // Experimental cloth-config dependencies API added in v8.4
    // Forward-ported to newer versions but not backported to older versions.
    constants["cloth_dependencies"] = sc.eval(meta.deps["cloth"], ">=8.4")

    replacements {
        string(current.parsed >= "26.0") {
            replace("accessWidener v2 named", "accessWidener v2 official")
            replace("GuiGraphics", "GuiGraphicsExtractor")
            replace("renderTextureOverlay", "extractTextureOverlay")
            replace("net.minecraft.client.renderer.state.CameraRenderState", "net.minecraft.client.renderer.state.level.CameraRenderState")
            replace("net.minecraft.client.renderer.state.LevelRenderState", "net.minecraft.client.renderer.state.level.LevelRenderState")
        }
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
            replace("GenericDirtMessageScreen", "GenericMessageScreen")
        }
        string(current.parsed >= "1.19") {
            replace("new net.minecraft.network.chat.TranslatableComponent(", "Component.translatable(")
            replace("new net.minecraft.network.chat.TextComponent(", "Component.literal(")
            replace("net.minecraft.network.chat.TextComponent.EMPTY", "Component.empty()")
        }
    }
}

tasks.named<Wrapper>("wrapper") {
    // Use "all" so we get sources and javadoc too
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "9.2.1"
}

// Move the changelog tasks to the "version" group
tasks.withType<BaseChangelogTask>().configureEach {
    group = "version"
}

changelog {
    // Use the mod_version in gradle.properties as the release version/tag.
    // Build tag/diff links using the github repo URL.
    version = meta.version
    repositoryUrl = meta.sourceUrl.toString()

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
    headerParserRegex = Regex("(\\d+(?:\\.\\d+)+(?:-[-a-z]+(?:\\.\\d+)?)?)")
}

tasks.generateReleaseMetadata {
    changelog = provider {
        val entry = project.changelog.getOrNull(meta.version) ?: project.changelog.getUnreleased()
        project.changelog.renderItem(
            entry.withHeader(false)
                .withLinks(false)
                .withEmptySections(false)
                .withSummary(true),
            outputType = Changelog.OutputType.MARKDOWN
        )
    }
}
