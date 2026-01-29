package net.xolt.freecam.gradle

import net.xolt.freecam.model.ProjectReleaseMetadata
import org.gradle.api.JavaVersion

/**
 * Convert this [JavaVersion] into a [ProjectReleaseMetadata]-format slug.
 *
 * Examples:
 * - `JavaVersion.VERSION_21`  → `"java_21"`
 * - `JavaVersion.VERSION_17`  → `"java_17"`
 * - `JavaVersion.VERSION_1_8` → `"java_8"`
 */
fun JavaVersion.toReleaseMetadataSlug() =
    "java_$majorVersion"