plugins {
    alias(libs.plugins.fletchingtable.fabric)
    id("freecam.loom-adapter")
    id("freecam.common")
}

dependencies {
    minecraft("com.mojang:minecraft:${meta.mc}")
    loomAdapter.applyMojangMappings()
    modCompileOnly(libs.fabric.loader)

    // Loader project should provide their own :common, :config and cloth-config dependencies
    compileOnly(project(path = commonNode.project.path, configuration = "namedElements"))
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${meta.deps["cloth"]}")
    compileOnly(project(":config"))
}
