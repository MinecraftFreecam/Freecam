plugins {
    id("fabric-loom") version "1.14-SNAPSHOT"
    id("freecam.loaders")
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

stonecutter {

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

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}")

    modImplementation("com.terraformersmc:modmenu:${commonMod.dep("modmenu")}") {
        exclude(module = "fabric-api")
    }

    modApi("me.shedaniel.cloth:cloth-config-fabric:${commonMod.dep("cloth")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    include("me.shedaniel.cloth:cloth-config-fabric:${commonMod.dep("cloth")}")
}

loom {
    accessWidenerPath = project(":common").file("src/main/resources/freecam.accesswidener")
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
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

publisher {
    listOf(curseDepends, modrinthDepends).forEach {
        it.required("fabric-api")
        it.optional("modmenu")
    }
}
