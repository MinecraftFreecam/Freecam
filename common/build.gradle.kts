import net.fabricmc.loom.task.MigrateClassTweakerMappingsTask
import net.fabricmc.loom.task.ValidateAccessWidenerTask

plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
    id("freecam.common")
}

fletchingTable {
    j52j.register("main") {
        prettyPrint = true
        extension("json", "**/*.json5")
    }
}

loom {
    // Loom unfortunately uses accessWidenerPath for two different purposes:
    // - AccessWidenerJarProcessor eagerly reads it while constructing parts of Loom's decompilation pipeline.
    // - Other tasks use it as their default input.
    //
    // The first requires an eagerly-written file, while the second should consume the task-generated access widener.
    //
    // We cannot easily override AccessWidenerJarProcessor, so we set the global property to an eagerly-written file
    // and explicitly configure loom's tasks below.
    accessWidenerPath = provider {
        stonecutter.process(
            file = rootDir.resolve("common/src/main/resources/freecam.accesswidener"),
            destination = "generated-eval/freecam.accesswidener"
        )
    }

    mixin {
        useLegacyMixinAp = false
    }
}

val i18nResources by configurations.registering {
    description = "The i18n project language files"
    isCanBeResolved = true
    isCanBeConsumed = false

    attributes {
        attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "directory")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    loomAdapter.applyMojangMappings()
    modCompileOnly(libs.fabric.loader)
    compileOnly(project(":config"))
    i18nResources(project(":i18n"))
}

// Since we set `loom.accessWidenerPath` to an eval-time written AW file above,
// explicitly configure loom's tasks to use the task-generated AW file.
tasks.processResources.map { it.destinationDir.resolve("freecam.accesswidener") }.let { awFile ->
    tasks.withType<ValidateAccessWidenerTask> { accessWidener = awFile }
    tasks.withType<MigrateClassTweakerMappingsTask> { inputFile = awFile }
}

tasks.processResources {
    from(i18nResources) {
        into("assets/${meta.id}/lang")
    }

    filesMatching("freecam-common.mixins.json5") {
        expand("mixinCompatLevel" to "JAVA_${meta.javaVersion}")
    }

    inputs.properties("java_version" to meta.javaVersion)

    duplicatesStrategy = DuplicatesStrategy.FAIL
}

configurations.create("generatedSourcesElements") {
    description = "Generated sources from the common project."
    isCanBeConsumed = true
    isCanBeResolved = false

    artifacts {
        add(name, tasks.stonecutterGenerate.map { it.destinationDir }) {
            builtBy(tasks.stonecutterGenerate)
        }
    }
}

configurations.create("processedResourcesElements") {
    description = "Processed resources from the common project."
    isCanBeConsumed = true
    isCanBeResolved = false

    artifacts {
        add(name, tasks.processResources.map { it.destinationDir }) {
            builtBy(tasks.processResources)
        }
    }
}
