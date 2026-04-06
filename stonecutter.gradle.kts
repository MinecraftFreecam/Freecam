// This file represents the version-less `rootProject` (:)
// Version-agnostic tasks should go here, as well as configuration for stonecutter itself

plugins {
    id("freecam.api")
    id("freecam.release-metadata")
    id("dev.kikugie.stonecutter")
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

    dependencies["gson"] = when {
        current.parsed >= "1.21.11" -> "2.13.2"
        current.parsed >= "1.21.4" -> "2.11.0"
        current.parsed >= "1.20.2" -> "2.10.1"
        current.parsed >= "1.19.3" -> "2.10"
        current.parsed >= "1.18.2" -> "2.8.9"
        current.parsed >= "1.18" -> "2.8.8"
        current.parsed >= "1.12" -> "2.8.0"
        else -> "2.2.4"
    }

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

val releaseNotes by configurations.registering {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    releaseNotes(project(":changelog", configuration = "releaseNotes"))
}

tasks.generateReleaseMetadata {
    changelog = releaseNotes.map { it.singleFile.readText() }

    // FIXME: the :changelog outgoing artifact should encode its dependencies,
    //  this project shouldn't need to know about the underlying task.
    //  See https://github.com/gradle/gradle/issues/24131
    dependsOn(gradle.includedBuild("changelog").task(":getReleaseNotes"))
}

tasks.named<Wrapper>("wrapper") {
    // Use "all" so we get sources and javadoc too
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "9.2.1"
}
