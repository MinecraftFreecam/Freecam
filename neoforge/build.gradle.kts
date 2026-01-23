plugins {
    alias(libs.plugins.moddev)
    alias(libs.plugins.fletchingtable.neoforge)
    id("freecam.loaders")
}

fletchingTable {
    accessConverter.register("main") {
        // Access widener file relative to `src/main/resources`
        // Converted to `META-INF/accesstransformer.cfg` by default
        add("freecam.accesswidener")
    }
}

neoForge {
    enable {
        version = commonMod.dep("neoforge")
    }
}

dependencies {
    api("me.shedaniel.cloth:cloth-config-neoforge:${commonMod.dep("cloth")}")
    jarJar(implementation("me.shedaniel.cloth:cloth-config-neoforge:${commonMod.dep("cloth")}") as Any)
}

neoForge {
    val at = project.file("build/resources/main/META-INF/accesstransformer.cfg");

    accessTransformers.from(at.absolutePath)
    validateAccessTransformers = true

    runs {
        register("client") {
            client()
            ideName = "NeoForge Client (${project.path})"
        }
//        register("server") {
//            server()
//            ideName = "NeoForge Server (${project.path})"
//        }
    }

    parchment {
        commonMod.depOrNull("parchment")
            ?.split('-', limit = 2)
            ?.also { (mc, mappings) ->
                minecraftVersion = mc
                mappingsVersion = mappings
            }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.jar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(":neoforge:${project.name}:processResources")
}

tasks.processResources {
    filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(commonExpansions)
    }

    filesMatching("pack.mcmeta") {
        expand(commonJsonExpansions)
    }

    inputs.properties(commonExpansions)
}

publisher {
    artifact.set(tasks.named("jar"))
}
