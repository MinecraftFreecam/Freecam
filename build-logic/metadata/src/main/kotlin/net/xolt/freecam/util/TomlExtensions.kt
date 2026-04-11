package net.xolt.freecam.util

import dev.eav.tomlkt.*
import java.io.File

internal inline fun <reified T> File.decodeTomlPath(vararg path: String): T {
    val table: TomlTable = bufferedReader().use { reader ->
        Toml.decodeFromReader(TomlNativeReader(reader))
    }
    val element = table.get(*path) ?: error("Missing ${path.joinToString(".")}")
    return Toml.decodeFromTomlElement(element)
}