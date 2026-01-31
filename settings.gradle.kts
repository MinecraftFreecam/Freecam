val isCi = System.getenv("CI") == "true"
gradle.startParameter.isParallelProjectExecutionEnabled = !isCi
gradle.startParameter.isBuildCacheEnabled = !isCi
gradle.startParameter.isConfigureOnDemand = !isCi

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.firstdark.dev/releases/")
        maven("https://maven.firstdark.dev/snapshots/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")
    }
    includeBuild("build-logic")
}

plugins {
    id("freecam.settings")
    id("freecam.modmetadata")
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        val projectVersions = loadStonecutterVersions()
        versions(projectVersions.values.flatten().distinct())
        projectVersions.forEach { (name, mcVersions) ->
            branch(name) { versions(mcVersions) }
        }
    }
}

rootProject.name = "freecam"