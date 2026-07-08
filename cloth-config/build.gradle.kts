import net.fabricmc.loom.task.FabricModJsonV1Task

plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
    id("freecam.common")
}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    loomAdapter.applyMojangMappings()
    modCompileOnly(libs.fabric.loader)

    // Loader project should provide their own :common and cloth-config dependencies
    compileOnly(project(path = commonNode.project.path, configuration = "namedElements"))
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
}

tasks {
    // A fabric.mod.json file is required for fabricloader to know the library's `version`
    val modJsonTask = register<FabricModJsonV1Task>("generateModJson") {
        description = "Generate fabric.mod.json"

        outputFile = layout.buildDirectory.dir("generated-mod-json").map {
            it.file("fabric.mod.json")
        }

        json {
            modId = "${meta.id}-cloth-config"
            name = "${meta.name} Cloth Config GUI"
            version = meta.version.toString()
            description = "Provides a ${meta.name} Config GUI when Cloth Config is installed."
            licenses = listOf(meta.license)

            client()
            // Currently bundled with Freecam, no need to specify a dependency
            // recommends("cloth-config", meta.reqs["cloth"].toString())

            customData = mapOf(
                "modmenu" to mapOf(
                    "parent" to meta.id,
                    "badges" to listOf("library"),
                ),
            )
        }
    }

    processResources {
        from(modJsonTask)
    }

    jar {
        // `GAMELIBRARY` is required to access Minecraft classes from ModLauncher 9 (1.17)
        val modType = if (sc.current.parsed >= "1.17") "GAMELIBRARY" else "LIBRARY"
        manifest.attributes(
            "FMLModType" to modType,
            "Automatic-Module-Name" to "${meta.id}.clothconfig",
        )
    }
}
