plugins {
    id("idea")
    id("java-library")
    id("maven-publish")
}

version = "${loader}-${currentMod.version}+mc${currentMod.mc}"
group = currentMod.group
base {
    archivesName = currentMod.id
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = javaVersion
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
    currentMod.depOrNull("neoforge_pr")?.let {
        maven {
            url = uri("https://prmaven.neoforged.net/NeoForge/pr${it}")
            content { includeModule("net.neoforged", "neoforge") }
        }
    }
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks.named("processResources") {
    dependsOn(":common:${project.name}:stonecutterGenerate")
}