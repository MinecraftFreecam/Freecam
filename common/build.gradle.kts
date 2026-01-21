plugins {
    id("freecam.common")
    id("fabric-loom") version "1.14-SNAPSHOT"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

stonecutter {

}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }
}

loom {
    accessWidenerPath = project(":common").file("src/main/resources/freecam.accesswidener")

    mixin {
        useLegacyMixinAp = false
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")
    mappings(loom.layered {
        officialMojangMappings()
        commonMod.depOrNull("parchment")?.let { parchmentVersionFull ->
            val parchmentAppendix = parchmentVersionFull.split('-')[0]
            val parchmentVersion = parchmentVersionFull.split('-')[1]
            parchment("org.parchmentmc.data:parchment-${parchmentAppendix}:$parchmentVersion@zip")
        }
    })

    compileOnly("org.spongepowered:mixin:0.8.5")
    modCompileOnly("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${commonMod.dep("cloth")}")
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
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}