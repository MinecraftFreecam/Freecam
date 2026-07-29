import net.fabricmc.loom.configuration.IncludeConfigurations.nestJars
import net.fabricmc.loom.task.FabricModJsonV1Task
import net.fabricmc.loom.task.MigrateClassTweakerMappingsTask
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.ValidateAccessWidenerTask

plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
    id("freecam.loaders")
    id("freecam.shadow")
}

fletchingTable {
    j52j.register("main") {
        prettyPrint = true
        extension("json", "**/*.json5")
    }
}

stonecutter replacements {
    string(sc.current.parsed >= "26.0") {
        replace("net.fabricmc.fabric.api.client.keybinding", "net.fabricmc.fabric.api.client.keymapping")
        replace("KeyBindingHelper", "KeyMappingHelper")
        replace("registerKeyBinding", "registerKeyMapping")
    }
}

val fabricApiModules =  buildList {
    val mc = sc.current.parsed
    sequenceOf(
        "fabric-api-base",
        "fabric-lifecycle-events-v1",
    ).forEach(::add)
    when {
        mc >= "1.21.9" -> add("fabric-resource-loader-v1")
        else -> add("fabric-resource-loader-v0")
    }
    when {
        mc >= "26.1" -> add("fabric-key-mapping-api-v1")
        else -> add("fabric-key-binding-api-v1")
    }
    sort()
}

val fabricApiVersion = requireNotNull(meta.deps["fabric_api"]) {
    "Missing deps.fabric_api for ${project.path}"
}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    loomAdapter.applyMojangMappings()
    modImplementation(libs.fabric.loader)

    fabricApiModules.forEach { name ->
        include(modImplementation(fabricApi.module(name, fabricApiVersion))!!)
    }

    // Note: cloth-config and our fabric.mod.json require the entire fabric-api at runtime
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api") {
        version { prefer(fabricApiVersion) }
        exclude(module = "fabric-loader")
    }

    modImplementation("com.terraformersmc:modmenu:${meta.deps["modmenu"]}") {
        exclude(module = "fabric-loader")
    }

    bundle(api(project(":config"))!!)

    sc.node.sibling("cloth-config")?.let {
        bundle(api(project(path = it.project.path, configuration = "namedElements"))!!)

        include(modApi("me.shedaniel.cloth:cloth-config-fabric") {
            version { prefer(meta.deps["cloth"]!!) }
            exclude(module = "fabric-api")
            exclude(module = "fabric-loader")
        })
    } ?: logger.warn("No :cloth-config project for ${project.path}")
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

// Since we set `loom.accessWidenerPath` to an eval-time written AW file above,
// explicitly configure loom's tasks to use the task-generated AW file.
tasks.processResources.map { it.destinationDir.resolve("freecam.accesswidener") }.let { awFile ->
    tasks.withType<ValidateAccessWidenerTask> { accessWidener = awFile }
    tasks.withType<MigrateClassTweakerMappingsTask> { inputFile = awFile }
}

val finalJarTask = if (loomAdapter.hasMappings) tasks.named<RemapJarTask>("remapJar") else tasks.shadowJar

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(finalJarTask.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${meta.buildDir}"))
    dependsOn(tasks.build)
}

tasks.generateReleaseMetadata {
    artifactFileName = finalJarTask.flatMap { it.archiveFileName }
}

tasks {
    val generateModJson by registering(FabricModJsonV1Task::class) {
        outputFile = layout.buildDirectory.dir("generated/mod-json").map {
            it.file("fabric.mod.json")
        }

        json {
            modId = meta.id
            name = meta.name
            version = meta.version.toString()
            description = meta.description
            licenses = listOf(meta.license)
            meta.authors.forEach(::author)
            icon("icon.png")

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

            depends("minecraft", meta.reqs["mc"]?.toString() ?: error("${project.path} missing reqs.mc"))
            depends("fabricloader", meta.reqs["fabric_loader"]?.toString() ?: error("${project.path} missing reqs.fabric_loader"))
            fabricApiModules.forEach { depends(it, "*") }
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

        filesMatching("freecam-fabric.mixins.json5") {
            expand("mixinCompatLevel" to "JAVA_${meta.javaVersion}")
        }

        inputs.properties("java_version" to meta.javaVersion)
    }

    if (loomAdapter.hasMappings) {
        shadowJar {
            archiveClassifier = "named"
        }
        named<RemapJarTask>("remapJar") {
            inputFile = shadowJar.flatMap { it.archiveFile }
            archiveClassifier = null
        }
    } else {
        nestJars(project, shadowJar, configurations.include)
    }
}
