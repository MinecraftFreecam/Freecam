import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import net.fabricmc.loom.task.FabricModJsonV1Task

plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
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

    sequenceOf(
        "fabric-lifecycle-events-v1",
        "fabric-key-binding-api-v1",
    ).map { name ->
        fabricApi.module(name, meta.deps["fabric_api"])
    }.forEach { module ->
        // TODO: include via jar-in-jar and drop `requires: fabric-api` from fabric.mod.json.
        // include(module)
        modImplementation(module)
    }

    // Note: cloth-config and our fabric.mod.json require the entire fabric-api at runtime
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api") {
        version { prefer(meta.deps["fabric_api"]) }
        exclude(module = "fabric-loader")
    }

    modImplementation("com.terraformersmc:modmenu:${meta.deps["modmenu"]}") {
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
    from(tasks.remapJar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${meta.version}"))
    dependsOn(tasks.build)
}

tasks {
    val generateModJson by registering(FabricModJsonV1Task::class) {
        outputFile = layout.buildDirectory.dir("generated-mod-json").map {
            it.file("fabric.mod.json")
        }

        json {
            modId = meta.id
            name = meta.name
            version = meta.version
            description = meta.description
            licenses = listOf(meta.license)
            meta.authors.forEach(::author)

            icon {
                size = 128
                path = "icon.png"
            }

            client()
            entrypoint("client", "net.xolt.freecam.fabric.FreecamFabric")
            entrypoint("modmenu", "net.xolt.freecam.fabric.ModMenuIntegration")

            accessWidener = "freecam.accesswidener"
            sequenceOf("common", "fabric").forEach {
                mixin {
                    environment = "client"
                    value = "freecam-$it.mixins.json"
                }
            }

            depends("minecraft", sc.properties.get<String>("fabric_mc_req"))
            depends("fabricloader", sc.properties.get<String>("fabric_loader_req"))
            depends(if (sc.current.parsed < "1.19.2") "fabric" else "fabric-api", "*")
            recommends("modmenu", "*")

            contactInformation = mapOf(
                "homepage" to meta.homepageUrl.toString(),
                "sources" to meta.sourceUrl.toString(),
                "issues" to meta.issuesUrl.toString(),
            )

            customData = mapOf(
                "modmenu" to mapOf(
                    "links" to mapOf(
                        "modmenu.crowdin" to meta.crowdinUrl.toString(),
                        "modmenu.curseforge" to meta.curseforgeUrl.toString(),
                        "modmenu.modrinth" to meta.modrinthUrl.toString(),
                        "modmenu.github_releases" to meta.githubReleasesUrl.toString(),
                    ),
                ),
            )
        }
    }

    processResources {
        from(generateModJson)

        filesMatching("freecam-fabric.mixins.json") {
            expand(commonExpansions)
        }

        inputs.properties(commonExpansions)
    }

    generateReleaseMetadata {
        artifactFileName = remapJar.flatMap { it.archiveFileName }
    }
}
