import dev.kikugie.stonecutter.StonecutterExperimentalAPI

plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
    id("freecam.loaders")
}

stonecutter replacements {
    string(sc.current.parsed >= "26.0") {
        replace("net.fabricmc.fabric.api.client.keybinding", "net.fabricmc.fabric.api.client.keymapping")
        replace("KeyBindingHelper", "KeyMappingHelper")
        replace("registerKeyBinding", "registerKeyMapping")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    if (loomAdapter.hasMappings) {
        mappings(loom.layered {
            officialMojangMappings()
            meta.parchment { mappings, mc ->
                parchment("org.parchmentmc.data:parchment-${mc}:$mappings@zip")
            }
        })
    }

    modImplementation("net.fabricmc:fabric-loader:${meta.deps["fabric_loader"]}")
    modApi("net.fabricmc.fabric-api:fabric-api:${meta.deps["fabric_api"]}") {
        exclude(module = "fabric-loader")
    }

    modImplementation("com.terraformersmc:modmenu:${meta.deps["modmenu"]}") {
        exclude(module = "fabric-api")
        exclude(module = "fabric-loader")
    }

    sc.node.sibling("cloth-config")?.let {
        include(it.project)
        api(project(path = it.project.path, configuration = "namedElements"))

        include("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
        modApi("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}") {
            exclude(module = "fabric-api")
            exclude(module = "fabric-loader")
        }
    } ?: logger.warn("No :cloth-config project for ${project.path}")
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
    from(loomAdapter.modJar.map { it.archiveFile })
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
        artifactFileName = loomAdapter.modJar.flatMap { it.archiveFileName }
    }
}
