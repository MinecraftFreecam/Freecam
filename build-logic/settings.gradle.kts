
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://maven.fabricmc.net") { name = "Fabric" }
            }
            filter { includeGroupAndSubgroups("net.fabricmc") }
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
}

include(
    "api",
    "api:plugin",
    "conventions",
    "i18n-plugin",
    "metadata",
    "release-metadata",
    "settings",
)

project(":api:plugin").projectDir = file("api-plugin")

rootProject.name = "build-logic"