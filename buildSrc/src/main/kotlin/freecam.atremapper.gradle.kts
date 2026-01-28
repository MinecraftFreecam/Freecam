import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.MappingWriter
import net.fabricmc.mappingio.adapter.MappingNsRenamer
import net.fabricmc.mappingio.format.MappingFormat
import net.fabricmc.mappingio.tree.MappingTree
import net.fabricmc.mappingio.tree.MemoryMappingTree
import net.xolt.freecam.gradle.MCVersionJson
import net.xolt.freecam.gradle.MCVersionManifest
import java.net.URI

val json = Json {
    ignoreUnknownKeys = true
}

val srgFile = layout.buildDirectory.file("mappings/srg.tsrg")
val mojMapFile = layout.buildDirectory.file("mappings/mojMap.txt")
val namedToSrgFile = layout.buildDirectory.file("mappings/namedToSrg.tsrg")
val processResources = tasks.named<ProcessResources>("processResources")

val generateNamedToSrg by tasks.registering {
    group = "mappings"
    dependsOn(downloadSrgMappings, downloadMojMappings)
    outputs.file(namedToSrgFile)

    doLast {
        val tree = MemoryMappingTree()

        mojMapFile.get().asFile.bufferedReader().use { reader ->
            val renamer = MappingNsRenamer(tree, mapOf("source" to "named", "target" to "official"))
            MappingReader.read(reader, MappingFormat.PROGUARD_FILE, renamer)
        }

        srgFile.get().asFile.bufferedReader().use { reader ->
            val renamer = MappingNsRenamer(tree, mapOf("obf" to "official"))
            MappingReader.read(reader, MappingFormat.TSRG_2_FILE, renamer)
        }

        MappingWriter.create(
            namedToSrgFile.get().asFile.toPath(),
            MappingFormat.TSRG_2_FILE,
        ).use { writer ->
            tree.accept(writer)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
val downloadMojMappings by tasks.registering {
    group = "mappings"
    outputs.file(mojMapFile)

    // Out of date when MC version changes
    inputs.property("minecraftVersion", currentMod.mc)

    doLast {
        val manifestUrl = URI("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").toURL()

        val manifest: MCVersionManifest = manifestUrl.openStream().use(json::decodeFromStream)

        val version = manifest.versions.firstOrNull { it.id == currentMod.mc }
            ?: error("Minecraft version ${currentMod.mc} not found in Mojang manifest")

        val versionUrl = URI(version.url).toURL()
        val versionJson: MCVersionJson = versionUrl.openStream().use(json::decodeFromStream)

        val mappingsUrl = URI(versionJson.downloads.clientMappings.url).toURL()
        mappingsUrl.openStream().use { mappings ->
            val out = mojMapFile.get().asFile
            out.parentFile.mkdirs()
            out.outputStream().use(mappings::copyTo)
        }
    }
}

val downloadSrgMappings by tasks.registering {
    group = "mappings"
    outputs.file(srgFile)

    // Out of date when MC version changes
    inputs.property("minecraftVersion", currentMod.mc)

    doLast {
        val mcpVersion = currentMod.mc
        val config = project.configurations.detachedConfiguration(
                project.dependencies.create("de.oceanlabs.mcp:mcp_config:${mcpVersion}@zip")
                )

        val zipFile = config.singleFile
        val outputDir = srgFile.get().asFile.parent

        copy {
            from(zipTree(zipFile).files.filter { it.name.equals("joined.tsrg") })
            into(outputDir)
            rename { srgFile.get().asFile.name }
        }
    }
}

val remapAtToSrg by tasks.registering {
    group = "mappings"
    description = "Remap AccessTransformer from named to SRG"
    dependsOn(generateNamedToSrg)

    // the unmapped file comes from fletching table, via processResources
    // We later overwrite this file
    val inputAt = processResources.map {
        it.destinationDir.resolve("META-INF/accesstransformer.cfg")
    }

    // outputAt is a staging file so that gradle can see whether anything changed
    val outputAt = layout.buildDirectory.file("srg_at/accesstransformer.cfg")

    inputs.file(inputAt)
    inputs.file(namedToSrgFile)
    outputs.file(outputAt)

    doLast {
        val remapper = MemoryMappingTree()

        namedToSrgFile.get().asFile.bufferedReader().use { reader ->
            MappingReader.read(reader, MappingFormat.TSRG_2_FILE, remapper)
        }

        outputAt.get().asFile.parentFile.mkdirs()

        val remappedLines = inputAt.get().readLines().map { line ->
            remapAtLine(line, remapper)
        }

        outputAt.get().asFile.writeText(remappedLines.joinToString("\n"))

        // Overwrite the processResources file
        copy {
            from(outputAt)
            into(inputAt.get().parentFile)
        }
    }
}

processResources {
    // Fletching Table generates accesstransformer.cfg during processResources execution.
    // We must remap it after processResources runs, and ensure downstream tasks see the
    // remapped file.
    finalizedBy(remapAtToSrg)
}

// Even though `processResources` is finalized by `remapAtToSrg`, that doesn't reliably
// cause Gradle to remap the AT file before running `createMinecraftArtifacts`.
// An explicit dependency seems to work.
tasks.matching { it.name == "createMinecraftArtifacts" }.configureEach {
    dependsOn(remapAtToSrg)
}

fun remapAtLine(line: String, remapper: MappingTree): String {
    val srgId = remapper.getNamespaceId("srg")

    val trimmed = line.trim()
    if (trimmed.isEmpty() || trimmed.startsWith("#")) return line

    val parts = trimmed.split(" ")
    if (parts.size < 3) return line

    val className = parts[1].replace('.', '/')
    val member = parts[2]

    val remappedMember = when {
        member.contains("(") -> {
            val name = member.substringBefore("(")
            val desc = member.substringAfter(name)
            remapper.getMethod(className, name, desc)?.getName(srgId) ?: name
        }
        else -> {
            remapper.getField(className, member, null)?.getName(srgId) ?: member
        }
    }

    val newLineParts = parts.take(2) + remappedMember + parts.drop(3)
    return newLineParts.joinToString(" ")
}