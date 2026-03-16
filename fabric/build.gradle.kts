import dev.kikugie.stonecutter.StonecutterExperimentalAPI

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loaders")
}

stonecutter {

}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    mappings(loom.layered {
        officialMojangMappings()
        meta.parchment { mappings, mc ->
            parchment("org.parchmentmc.data:parchment-${mc}:$mappings@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:${meta.deps["fabric_loader"]}")
    modApi("net.fabricmc.fabric-api:fabric-api:${meta.deps["fabric_api"]}") {
        exclude(module = "fabric-loader")
    }

    modImplementation("com.terraformersmc:modmenu:${meta.deps["modmenu"]}") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }

    modApi("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }
    include("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
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
    into(rootProject.layout.buildDirectory.file("libs/${meta.version}"))
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
