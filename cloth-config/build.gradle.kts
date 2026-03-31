plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
    id("freecam.common")
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
    modCompileOnly("net.fabricmc:fabric-loader:${meta.deps["fabric_loader"]}")

    // Loader project should provide their own :common and cloth-config dependencies
    compileOnly(project(path = commonNode.project.path, configuration = "namedElements"))
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
}

tasks.jar {
    // `GAMELIBRARY` is required to access Minecraft classes from ModLauncher 9 (1.17)
    val modType = if (sc.current.parsed >= "1.17") "GAMELIBRARY" else "LIBRARY"
    manifest.attributes(
        "FMLModType" to modType,
        "Automatic-Module-Name" to "freecam.clothconfig",
    )
}
