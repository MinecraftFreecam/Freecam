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
    accessWidenerPath = stonecutterBuild.process(project(":common").file("src/main/resources/freecam.accesswidener"), "build/stonecutter/processed.aw")

    mixin {
        useLegacyMixinAp = false
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${currentMod.mc}")
    mappings(loom.layered {
        officialMojangMappings()
        currentMod.parchment { mappings, mc ->
            parchment("org.parchmentmc.data:parchment-${mc}:$mappings@zip")
        }
    })

    compileOnly("org.spongepowered:mixin:${currentMod.dep("mixin")}")
    modCompileOnly("net.fabricmc:fabric-loader:${currentMod.dep("fabric_loader")}")
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${currentMod.dep("cloth")}")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }

        add(commonResources.name, tasks.processResources)
    }
}

tasks.processResources {
    filesMatching("freecam-common.mixins.json") {
        expand(commonJsonExpansions)
    }
}