import io.github.z4kn4fein.semver.constraints.toMavenFormat
import kotlinx.serialization.json.*
import net.neoforged.moddevgradle.legacyforge.internal.MinecraftMappings
import net.xolt.freecam.gradle.ForgeModsTomlTask

plugins {
    alias(libs.plugins.moddev.legacy)
    alias(libs.plugins.fletchingtable)
    id("freecam.loaders")
    id("freecam.atremapper")
    id("freecam.fml")
}

val json = Json { prettyPrint = true }

val forgeVersion = requireNotNull(meta.deps["forge_version"]) {
    "Missing deps.forge_version for ${project.path}"
}

val refmapName = "mixins.freecam.refmap.json"
val mixinConfigNames = listOf(
    "freecam-common.mixins.json",
    "freecam-forge.mixins.json",
)

stonecutter replacements {
    string(sc.eval(forgeVersion, ">= 41")) {
        replace( "net.minecraftforge.client.ConfigGuiHandler", "net.minecraftforge.client.ConfigScreenHandler")
        replace( "ConfigGuiHandler.ConfigGuiFactory", "ConfigScreenHandler.ConfigScreenFactory")
    }
    string(sc.eval(forgeVersion, "> 37.1.1")) {
        replace("net.minecraftforge.fmlclient.ConfigGuiHandler", "net.minecraftforge.client.ConfigGuiHandler")
        replace("net.minecraftforge.fmlclient.registry.ClientRegistry", "net.minecraftforge.client.ClientRegistry")
    }
}

fletchingTable {
    j52j.register("main") {
        prettyPrint = true
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
        forgeVersion = "${meta.mc}-${meta.deps["forge_version"]}"
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

/**
 * Wrapper for [jarJar], only applied on Forge versions that support jar-in-jar.
 *
 * Jar-in-jar added in [Forge 40.1.60](https://maven.minecraftforge.net/net/minecraftforge/forge/1.18.2-40.1.60/forge-1.18.2-40.1.60-changelog.txt)
 */
fun DependencyHandler.include(dependency: Any) {
    if (sc.eval(forgeVersion, "<40.1.60")) return
    jarJar(dependency)
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:26.0.2")
    annotationProcessor(libs.sponge.mixin) {
        artifact { classifier = "processor" }
    }
    sc.node.sibling("cloth-config")?.let {
        val clothVersion = requireNotNull(meta.deps["cloth"]) {
            "Missing deps.cloth for ${project.path}"
        }
        val clothConstraint = requireNotNull(meta.reqs["cloth"]) {
            "Missing reqs.cloth for ${project.path}"
        }
        // `jarJar` requires a SRG dependency, which we don't have for `:cloth-config`.
        // Instead, we can include named-classes in jar and reobfJar will remap them.
        bundle(implementation(project(path = it.project.path, configuration = "namedElements"))!!)
        include(modImplementation("me.shedaniel.cloth:cloth-config-forge") {
            version {
                prefer(clothVersion)
                strictly(clothConstraint.toMavenFormat())
            }
        })
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

    mods {
        register(meta.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

mixin {
    add(sourceSets.main.get(), refmapName)
    mixinConfigNames.forEach(::config)
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.named<Jar>("reobfJar").map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${meta.buildDir}"))
    dependsOn("build")
}

val generateModsTomlTask = tasks.register<ForgeModsTomlTask>("generateModsToml") {
    description = "Generate the mods.toml file"

    toml {
        loaderVersion = meta.reqs["forge_loader"]?.toMavenFormat()
        issueTrackerURL = meta.issuesUrl.toString()
        license = meta.license

        mod(meta.id) {
            displayName = meta.name
            version = meta.version.toString()
            description = meta.description
            authors = meta.authors.joinToString(", ")
            displayURL = meta.homepageUrl.toString()
            logoFile = "icon.png"
            logoBlur = true
        }

        dependency(meta.id, "minecraft") {
            versionRange = meta.reqs["mc"]?.toMavenFormat()
            ordering = "NONE"
            side = "CLIENT"
        }
        dependency(meta.id, "forge") {
            versionRange = meta.reqs["forge_version"]?.toMavenFormat()
            ordering = "NONE"
            side = "CLIENT"
        }
        dependency(meta.id, "cloth_config") {
            versionRange = meta.reqs["cloth"]?.toMavenFormat()
            mandatory = false
            ordering = "NONE"
            side = "CLIENT"
        }
    }
}

tasks.processResources {
    from(generateModsTomlTask) {
        into("META-INF")
    }

    filesMatching("freecam-forge.mixins.json") {
        expand("mixinCompatLevel" to "JAVA_${meta.javaVersion}")
    }

    inputs.properties(
        "java_version" to meta.javaVersion,
        "mixinConfigs" to mixinConfigNames,
        "mixinRefmap" to refmapName,
    )

    doLast {
        // Add the refmap to mixin config files
        destinationDir.listFiles { file -> file.isFile && mixinConfigNames.contains(file.name) }.forEach { file ->
            file.inputStream()
                .use { stream -> json.decodeFromStream<MutableMap<String, JsonElement>>(stream) }
                .let { config ->
                    config["refmap"] = JsonPrimitive(refmapName)
                    file.outputStream().use { stream -> json.encodeToStream(config, stream) }
                }
        }
    }
}

tasks.jar {
    from(provider { bundle.map(::zipTree) }) {
        exclude(
            "${meta.id}.LICENSE",
            "META-INF/mods.toml",
            "META-INF/*.MF",
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
        )
    }

    manifest.attributes(
        "MixinConfigs" to mixinConfigNames.joinToString(","),
    )

    duplicatesStrategy = DuplicatesStrategy.FAIL
    finalizedBy("reobfJar")
}
