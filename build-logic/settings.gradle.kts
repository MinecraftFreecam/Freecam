
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.firstdark.dev/releases/")
        maven("https://maven.firstdark.dev/snapshots/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
    }
}

include(
    "api",
    "bump-version",
    "conventions",
    "settings",
)

rootProject.name = "build-logic"