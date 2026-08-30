// Improve dependency graph ordering
// https://docs.gradle.org/9.7.0/userguide/upgrading_version_9.html#dependency_resolution_ordering
// TODO: remove when it's the default (in Gradle 10)
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://jitpack.io") { name = "JitPack" }
            }
            filter {
                includeModule("com.github.MinecraftFreecam.Publisher", "schema")
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
                maven("https://maven.neoforged.net/releases") { name = "Neoforge" }
            }
            filter { includeGroupAndSubgroups( "net.neoforged") }
        }
        exclusiveContent {
            forRepositories(
                maven("https://maven.kikugie.dev/releases") { name = "KikuGie" },
                maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie snapshots" },
            )
            filter { includeGroupAndSubgroups("dev.kikugie") }
        }
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
}

plugins {
    id("freecam.settings")
    id("freecam.modmetadata")
}

stonecutter {
    create(rootProject, file("stonecutter.settings.toml"))
}

include("branding", "config", "i18n")

rootProject.name = "freecam"
