plugins {
    id("idea")
    id("java-library")
    id("maven-publish")
}

version = "${loader}-${commonMod.version}+mc${commonMod.mc}"
group = commonMod.group
base {
    archivesName = commonMod.id
}

val requiredJava = when {
    stonecutterBuild.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    stonecutterBuild.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    stonecutterBuild.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}
val javaVersion = requiredJava.majorVersion.toInt()
val javaLanguageVersion = JavaLanguageVersion.of(javaVersion)

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = javaVersion
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(javaLanguageVersion)
    })
}

java {
    toolchain {
        languageVersion.set(javaLanguageVersion)
    }
    withSourcesJar()
}

tasks.named<Jar>("jar") {
    from("LICENSE")
}

repositories {
    mavenCentral()
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.neoforged.net/releases/")
    if (commonMod.depOrNull("neoforge_pr")  != null) {
        maven {
            url = uri("https://prmaven.neoforged.net/NeoForge/pr${commonMod.dep("neoforge_pr")}")
            content { includeModule("net.neoforged", "neoforge") }
        }
    }
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks {
    processResources {
        val expandProps = mapOf(
            "javaVersion" to commonMod.propOrNull("java.version"),
            "modId" to commonMod.id,
            "modName" to commonMod.name,
            "modVersion" to commonMod.version,
            "modGroup" to commonMod.group,
            "modAuthors" to commonMod.authors.split(',').joinToString(", "),
            "modAuthorsJson" to commonMod.authors.split(',').joinToString("\", \""),
            "modDescription" to commonMod.description,
            "modLicense" to commonMod.license,
            "modHomepage" to commonMod.homepage,
            "modSource" to commonMod.source,
            "modIssues" to commonMod.issues,
            "modGhReleases" to commonMod.ghReleases,
            "modCurseforge" to commonMod.curseforge,
            "modModrinth" to commonMod.modrinth,
            "modCrowdin" to commonMod.crowdin,
            "minecraftVersion" to commonMod.propOrNull("minecraft_version"),
            "fabricLoaderVersion" to commonMod.depOrNull("fabric_loader"),
            "fabricLoaderReq" to commonMod.depOrNull("fabric_loader_req"),
            "fabricMcReq" to commonMod.depOrNull("fabric_mc_req"),
            "fabricApiVersion" to commonMod.depOrNull("fabric_api"),
            "neoForgeVersion" to commonMod.depOrNull("neoforge"),
            "neoforgeLoaderReq" to commonMod.depOrNull("neoforge_loader_req"),
            "neoforgeReq" to commonMod.depOrNull("neoforge_req"),
            "neoforgeMcReq" to commonMod.depOrNull("neoforge_mc_req"),
            "forgeVersion" to commonMod.depOrNull("forge"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

        val jsonExpandProps = expandProps.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
            expand(expandProps)
        }

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json")) {
            expand(jsonExpandProps)
        }

        inputs.properties(expandProps)
    }
}

tasks.named("processResources") {
    dependsOn(":common:${project.name}:stonecutterGenerate")
}