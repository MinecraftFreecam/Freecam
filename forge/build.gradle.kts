plugins {
    alias(libs.plugins.moddev.legacy)
    alias(libs.plugins.fletchingtable)
    id("freecam.loaders")
    id("freecam.atremapper")
}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }

    accessConverter.register("main") {
        // During processResources, Fletching Table will exclude this file
        // and generate a META-INF/accesstransformer.cfg from it
        add("freecam.accesswidener")
    }
}

legacyForge {
    enable {
        forgeVersion = "${meta.mc}-${meta.deps["forge"]}"
    }
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:26.0.2")
    annotationProcessor("org.spongepowered:mixin:${meta.deps["mixin"]}:processor")
    forgeDependency(group = "me.shedaniel.cloth", name = "cloth-config-forge", version = meta.deps["cloth"])
}

legacyForge {
    // Use the SRG-mapped accesstransformer
    accessTransformers.from(tasks.remapAtToSrg.map { it.outputs.files.singleFile })
    validateAccessTransformers = true

    runs {
        register("client") {
            client()
            ideName = "Forge Client (${project.path})"
        }
//        register("server") {
//            server()
//            ideName = "Forge Server (${project.path})"
//        }
    }

    parchment {
        meta.parchment { mappings, mc ->
            minecraftVersion = mc
            mappingsVersion = mappings
        }
    }

    mods {
        register(meta.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

mixin {
    add(sourceSets.main.get(), "mixins.freecam.refmap.json")
    config("freecam-common.mixins.json")
    config("freecam-forge.mixins.json")
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.jar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${meta.version}"))
    dependsOn("build")
}

tasks.processResources {
    filesMatching(listOf("META-INF/mods.toml", "META-INF/forge.mods.toml")) {
        expand(commonExpansions)
    }

    filesMatching("freecam-forge.mixins.json") {
        expand(commonJsonExpansions)
    }

    filesMatching("pack.mcmeta") {
        expand(commonJsonExpansions)
    }

    inputs.properties(commonExpansions)
}

tasks.jar {
    finalizedBy("reobfJar")
}

fun DependencyHandlerScope.forgeDependency(group: String, name: String, version: String) {
    compileOnly(group, name, version)
    modRuntimeOnly(group, name, version)
    jarJar(modImplementation(group, name, version))
}