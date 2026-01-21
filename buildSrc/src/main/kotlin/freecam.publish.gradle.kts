import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogPluginExtension

plugins {
    java
    id("com.hypherionmc.modutils.modpublisher")
}

publisher {
    curseID = "557076"
    modrinthID = "XeEZ3fK2"

    github {
        // Extract our repo slug from the github URL
        repo(rootProject.property("mod.source").toString().removePrefix("https://github.com/"))

        // Canonical tag (not the annotated build tag)
        tag("v${rootProject.property("mod.version")}")
        draft(true)
    }

    // Format display name, e.g. "1.2.4 for MC 1.20.4 (fabric)"
    displayName =
        "${rootProject.property("mod.version")} for MC ${rootProject.property("minecraft_version")} (${project.name})"

    projectVersion = project.version.toString()
    versionType = rootProject.property("release_type").toString()
    curseEnvironment = "client"

    loaders = listOf(project.name)
    javaVersions = listOf(project.java.targetCompatibility)

    // Get the changelog entry using the changelog plugin
    changelog.set(provider {
        val plugin = rootProject.extensions.getByType(ChangelogPluginExtension::class.java)
        val version = rootProject.property("mod_version").toString()

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
        val primary = rootProject.property("minecraft_version").toString()
        val common = rootProject.findProperty(prop)?.toString().orEmpty()
        val specific =
            rootProject.findProperty("${project.name}_$prop")?.toString().orEmpty()

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
