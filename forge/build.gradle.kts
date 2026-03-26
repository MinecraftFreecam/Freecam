import net.neoforged.moddevgradle.legacyforge.internal.MinecraftMappings

plugins {
    alias(libs.plugins.moddev.legacy)
    alias(libs.plugins.fletchingtable)
    id("freecam.loaders")
    id("freecam.atremapper")
}

stonecutter replacements {
    val forge = meta.deps["forge"]
    string(sc.eval(forge, ">= 41")) {
        replace( "net.minecraftforge.client.ConfigGuiHandler", "net.minecraftforge.client.ConfigScreenHandler")
        replace( "ConfigGuiHandler.ConfigGuiFactory", "ConfigScreenHandler.ConfigScreenFactory")
    }
    string(sc.eval(forge, "> 37.1.1")) {
        replace("net.minecraftforge.fmlclient.ConfigGuiHandler", "net.minecraftforge.client.ConfigGuiHandler")
        replace("net.minecraftforge.fmlclient.registry.ClientRegistry", "net.minecraftforge.client.ClientRegistry")
    }
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

// Include bundled dependencies in `jar`.
// Not suitable for third-party libs — consider using the gradleup shadow plugin.
val bundle by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false

    attributes {
        attribute(MinecraftMappings.ATTRIBUTE, objects.named(MinecraftMappings.NAMED))
    }
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:26.0.2")
    annotationProcessor("org.spongepowered:mixin:${meta.deps["mixin"]}:processor")
    sc.node.sibling("cloth-config")?.let {
        // `jarJar` requires a SRG dependency, which we don't have for `:cloth-config`.
        // Instead, we can include named-classes in jar and reobfJar will remap them.
        bundle(implementation(project(path = it.project.path, configuration = "namedElements")) as Any)
        forgeDependency(group = "me.shedaniel.cloth", name = "cloth-config-forge", version = meta.deps["cloth"])
    } ?: logger.warn("No :cloth-config project for ${project.path}")
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
    from(tasks.named<Jar>("reobfJar").map { it.archiveFile })
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
    from(provider { bundle.map(::zipTree) }) {
        exclude(
            "META-INF/mods.toml",
            "META-INF/*.MF",
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
        )
    }
    duplicatesStrategy = DuplicatesStrategy.FAIL
    finalizedBy("reobfJar")
}

fun DependencyHandlerScope.forgeDependency(group: String, name: String, version: String) {
    modCompileOnly(group, name, version)
    modRuntimeOnly(group, name, version)
    jarJar(modImplementation(group, name, version))
}