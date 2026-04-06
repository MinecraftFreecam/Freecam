package net.xolt.freecam.i18n

import org.gradle.api.tasks.Input

data class KeyTransformation(
    @get:Input val name: String,
    @get:Input val keepOriginal: Boolean,
    @get:Input val names: List<String>,
)
