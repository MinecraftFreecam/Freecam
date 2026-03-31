plugins {
    idea
    `java-library`
    `maven-publish`
}

version = "${meta.loader}-${meta.version}+mc${meta.mc}"
base {
    archivesName = meta.id
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = meta.javaVersion
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(meta.javaVersion)
    withSourcesJar()
}

tasks.named<Jar>("jar") {
    from(rootDir.resolve("LICENSE")) {
        rename { "${meta.id}.$it" }
    }
}

repositories {
    meta.parchment { _, _ ->
        exclusiveContent {
            forRepository {
                maven("https://maven.parchmentmc.org") { name = "Parchment" }
            }
            filter { includeGroup("org.parchmentmc.data") }
        }
    }
    maven("https://maven.neoforged.net/releases") {
        name = "NeoForge"
        content { includeGroup("net.neoforged") }
    }
    meta.deps.orNull("neoforge_pr").takeUnless { it.isNullOrBlank() }?.let {
        exclusiveContent {
            forRepository {
                maven("https://prmaven.neoforged.net/NeoForge/pr$it") {
                    name = "NeoForge PR#$it"
                }
            }
            filter { includeModule("net.neoforged", "neoforge") }
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.fabricmc.net") { name = "Fabric" }
        }
        filter { includeGroupAndSubgroups("net.fabricmc") }
    }
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.shedaniel.me") { name = "Shedaniel" }
        }
        filter { includeGroup("me.shedaniel.cloth") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.terraformersmc.com") { name = "TerraformersMC" }
        }
        filter { includeGroup("com.terraformersmc") }
    }
    mavenCentral()
}

tasks.processResources {
    dependsOn(tasks.named("stonecutterGenerate"))
}