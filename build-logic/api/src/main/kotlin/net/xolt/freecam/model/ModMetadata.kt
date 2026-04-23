package net.xolt.freecam.model

interface PropertyProvider : Iterable<Pair<String, String>> {
    operator fun get(prop: String): String
    fun orNull(prop: String): String?
    fun asSequence(): Sequence<Pair<String, String>>
    override fun iterator(): Iterator<Pair<String, String>> =
        asSequence().iterator()
}

interface ModMetadata : StaticModMetadata {
    val mc: String
    val loader: String
    val description: String
    val properties: PropertyProvider
    val mod: PropertyProvider
    val deps: PropertyProvider
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
