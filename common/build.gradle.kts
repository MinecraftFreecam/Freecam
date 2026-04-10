import dev.kikugie.stonecutter.StonecutterExperimentalAPI

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
    // Loom reads the AW during configuration, so the :stonecutterGenerate one is too late
    // We still use the task-generated AW during the actual build
    accessWidenerPath = provider {
        @OptIn(StonecutterExperimentalAPI::class)
        stonecutter.process(
            file = rootDir.resolve("common/src/main/resources/freecam.accesswidener"),
            destination = "build/generated-eval/freecam.accesswidener"
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
    i18nResources(project(":i18n"))
}

tasks.processResources {
    from(i18nResources) {
        into("assets/${meta.id}/lang")
    }

    filesMatching("freecam-common.mixins.json5") {
        expand(commonExpansions)
    }

    duplicatesStrategy = DuplicatesStrategy.FAIL
}