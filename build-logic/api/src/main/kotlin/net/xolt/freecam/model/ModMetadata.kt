package net.xolt.freecam.model

interface ModMetadata : StaticModMetadata {
    val mc: String
    val loader: String
    val description: String
    val properties: Map<String, String>
    val mod: Map<String, String>
    val deps: Map<String, String>
    val relationships: List<Relationship>
    val supportedMinecraftVersions: List<String>
    val javaVersion: Int
}

interface StaticModMetadata {
    val id: String
    val name: String
    val group: String
    val version: String
    val releaseType: ReleaseType
    val authors: List<String>
    val license: String
    val homepageUrl: UrlString
    val sourceUrl: UrlString
    val issuesUrl: UrlString
    val githubReleasesUrl: UrlString
    val curseforgeUrl: UrlString
    val curseforgeId: ULong
    val modrinthUrl: UrlString
    val modrinthId: String
    val crowdinUrl: UrlString
}
