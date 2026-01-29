plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loaders")

}

stonecutter {

}

dependencies {
    minecraft("com.mojang:minecraft:${currentMod.mc}")
    mappings(loom.layered {
        officialMojangMappings()
        currentMod.parchment { mappings, mc ->
            parchment("org.parchmentmc.data:parchment-${mc}:$mappings@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:${currentMod.dep("fabric_loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${currentMod.dep("fabric_api")}") {
        exclude(module = "fabric-loader")
    }

    modImplementation("com.terraformersmc:modmenu:${currentMod.dep("modmenu")}") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }

    modApi("me.shedaniel.cloth:cloth-config-fabric:${currentMod.dep("cloth")}") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }
    include("me.shedaniel.cloth:cloth-config-fabric:${currentMod.dep("cloth")}")
}

loom {
    accessWidenerPath = stonecutterBuild.process(project(":common").file("src/main/resources/freecam.accesswidener"), "build/stonecutter/processed.aw")
    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }
        getByName("server") {
//            server()
//            configName = "Fabric Server"
//            ideConfigGenerated(true)
            ideConfigGenerated(false)
        }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${currentMod.version}"))
    dependsOn(tasks.build)
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(commonJsonExpansions)
        }

        filesMatching("freecam-fabric.mixins.json") {
            expand(commonJsonExpansions)
        }

        inputs.properties(commonExpansions)
    }

    generateReleaseMetadata {
        artifactFileName = remapJar.flatMap { it.archiveFileName }
    }
}
