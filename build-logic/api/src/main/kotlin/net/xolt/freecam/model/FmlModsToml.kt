package net.xolt.freecam.model

import dev.eav.tomlkt.TomlMultilineString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see [`mods.toml`](https://docs.minecraftforge.net/en/1.21.x/gettingstarted/modfiles/#modstoml)
 * @see [`neoforge.mods.toml`](https://docs.neoforged.net/docs/gettingstarted/modfiles/#neoforgemodstoml)
 */
@Serializable
data class FmlModsToml(
    val modLoader: String,
    val loaderVersion: String,
    val license: String,
    val issueTrackerURL: String? = null,

    val mods: List<FmlModEntry> = emptyList(),
    val dependencies: Map<String, List<FmlDependencyEntry>>? = null,

    /** NeoForge only */
    val accessTransformers: List<FmlAccessTransformerEntry>? = null,
    /** NeoForge only */
    val mixins: List<FmlMixinEntry>? = null,
)

/**
 * @see [Forge docs](https://docs.minecraftforge.net/en/1.21.x/gettingstarted/modfiles/#mod-specific-properties)
 * @see [NeoForge docs](https://docs.neoforged.net/docs/gettingstarted/modfiles/#mod-specific-properties)
 */
@Serializable
data class FmlModEntry(
    val modId: String,
    val version: String,
    val displayName: String,
    @TomlMultilineString
    val description: String? = null,
    val logoFile: String? = null,
    val logoBlur: Boolean? = null,
    val authors: String? = null,
    val credits: String? = null,
    val displayURL: String? = null,
    val updateJSONURL: String? = null,
)

/**
 * @see [Forge docs](https://docs.minecraftforge.net/en/1.21.x/gettingstarted/modfiles/#dependency-configurations)
 * @see [NeoForge docs](https://docs.neoforged.net/docs/gettingstarted/modfiles/#dependency-configurations)
 */
@Serializable
data class FmlDependencyEntry(
    val modId: String,
    val versionRange: String,
    val ordering: String? = null,
    val side: String? = null,
    /** Forge only (deprecated by NeoForge) */
    val mandatory: Boolean? = null,
    /** NeoForge only (replaces [mandatory]) */
    val type: FmlDependencyType? = null,
)

/**
 * @see [NeoForge docs](https://docs.neoforged.net/docs/gettingstarted/modfiles/#dependency-configurations)
 */
@Serializable
enum class FmlDependencyType {
    @SerialName("required") REQUIRED,
    @SerialName("optional") OPTIONAL,
    @SerialName("incompatible") INCOMPATIBLE,
    @SerialName("discouraged") DISCOURAGED;
}

/**
 * @see [NeoForge docs](https://docs.neoforged.net/docs/gettingstarted/modfiles/#mixin-configuration-properties)
 */
@Serializable
data class FmlMixinEntry(
    /** The location of the mixin configuration file. */
    val config: String,

    /** Mod IDs that must be present for these mixins to be applied. */
    val requiredMods: List<String>? = null,
)

/**
 * @see [NeoForge docs](https://docs.neoforged.net/docs/gettingstarted/modfiles/#access-transformer-specific-properties)
 */
@Serializable
data class FmlAccessTransformerEntry(val file: String)
