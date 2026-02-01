import dev.kikugie.stonecutter.StonecutterExperimentalAPI

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.common")
}

stonecutter {

}

fletchingTable {
    j52j.register("main") {
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

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    mappings(loom.layered {
        officialMojangMappings()
        meta.parchment { mappings, mc ->
            parchment("org.parchmentmc.data:parchment-${mc}:$mappings@zip")
        }
    })

    compileOnly("org.spongepowered:mixin:${meta.deps["mixin"]}")
    modCompileOnly("net.fabricmc:fabric-loader:${meta.deps["fabric_loader"]}")
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
}

tasks.processResources {
    filesMatching("freecam-common.mixins.json") {
        expand(commonJsonExpansions)
    }
}