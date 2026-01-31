import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogPluginExtension

plugins {
    java
    id("com.hypherionmc.modutils.modpublisher")
}

publisher {
    curseID = meta.curseforgeId
    modrinthID = meta.modrinthId

    github {
        // Extract our repo slug from the github URL
        repo(meta.sourceUrl.toString().removePrefix("https://github.com/"))

        // Canonical tag (not the annotated build tag)
        tag("v${meta.version}")
        draft(true)
    }

    // Format display name, e.g. "1.2.4 for MC 1.20.4 (fabric)"
    displayName =
        "${meta.version} for MC ${currentMod.mc} (${loader})"

    projectVersion = meta.version
    versionType = meta.releaseType.toString()
    curseEnvironment = "client"

    loaders = listOf(project.name)
    javaVersions = listOf(project.java.targetCompatibility)

    // Get the changelog entry using the changelog plugin
    changelog.set(provider {
        val plugin = rootProject.extensions.getByType(ChangelogPluginExtension::class.java)
        val version = meta.version

        if (!plugin.has(version)) {
            logger.warn("No changelog for \"$version\". Using \"unreleased\" instead.")
        }

        val logEntry = (plugin.getOrNull(version) ?: plugin.getUnreleased())
            .withHeader(false)
            .withLinks(false)
            .withEmptySections(false)
            .withSummary(true)

        plugin.renderItem(logEntry, Changelog.OutputType.MARKDOWN)
    })

    // Supported game versions
    gameVersions.set(provider {
        val prop = "supported_mc_versions"
        val primary = currentMod.mc
        val common = currentMod.propOrNull(prop).orEmpty()
        val specific =
            rootProject.findProperty("${loader}_$prop")?.toString().orEmpty()

        (common.split(",") + specific.split(",") + primary)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    })

    listOf(curseDepends, modrinthDepends).forEach {
        it.embedded("cloth-config")
    }

    val ci = System.getenv("CI") != null
    val dummy = if (ci) "" else "default"

    apiKeys {
        github(
            findProperty("github_token")?.toString()
                ?: System.getenv("GITHUB_TOKEN")
                ?: System.getenv("GH_TOKEN")
                ?: dummy
        )
        curseforge(
            findProperty("curseforge_token")?.toString()
                ?: System.getenv("CURSEFORGE_TOKEN")
                ?: dummy
        )
        modrinth(
            findProperty("modrinth_token")?.toString()
                ?: System.getenv("MODRINTH_TOKEN")
                ?: dummy
        )
    }

    debug = !ci
}

tasks.named("publish") {
    dependsOn(tasks.named("publishMod"))
}
