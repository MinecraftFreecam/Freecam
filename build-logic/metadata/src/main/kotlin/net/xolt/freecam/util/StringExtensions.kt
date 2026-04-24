package net.xolt.freecam.util

internal fun String.withSuffix(suffix: String) =
    if (endsWith(suffix)) this else this + suffix
