plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.common")
}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    mappings(loom.layered {
        officialMojangMappings()
        meta.parchment { mappings, mc ->
            parchment("org.parchmentmc.data:parchment-${mc}:$mappings@zip")
        }
    })
    modCompileOnly("net.fabricmc:fabric-loader:${meta.deps["fabric_loader"]}")

    // Loader project should provide their own :common and cloth-config dependencies
    compileOnly(project(path = commonNode.project.path, configuration = "namedElements"))
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
}
