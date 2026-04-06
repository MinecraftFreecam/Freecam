val isCi = System.getenv("CI") == "true"
gradle.startParameter.isParallelProjectExecutionEnabled = !isCi
gradle.startParameter.isBuildCacheEnabled = !isCi
gradle.startParameter.isConfigureOnDemand = !isCi

pluginManagement {
    repositories {
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

include(
    "i18n",
    "publish",
    "publish:api",
    "publish:cli",
    "publish:platforms",
)

includeBuild("changelog")

rootProject.name = "freecam"