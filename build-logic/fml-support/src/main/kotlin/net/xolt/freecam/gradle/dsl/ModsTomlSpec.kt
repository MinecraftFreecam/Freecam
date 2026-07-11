package net.xolt.freecam.gradle.dsl

import net.xolt.freecam.model.FmlDependencyEntry
import net.xolt.freecam.model.FmlModEntry
import net.xolt.freecam.model.FmlModsToml
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.property

sealed class ModsTomlSpec<MOD_SPEC : ModEntrySpec, DEPENDENCY_SPEC : DependencyEntrySpec>(
    @Internal protected val objects: ObjectFactory,
) {
    @get:Input val modLoader = objects.property<String>().convention("javafml")
    @get:Input abstract val loaderVersion: Property<String>
    @get:Input abstract val license: Property<String>
    @get:Input @get:Optional abstract val issueTrackerURL: Property<String>

    @get:Nested
    internal val mods = mutableMapOf<String, MOD_SPEC>()

    @get:Nested
    internal val dependencies = mutableMapOf<String, MutableMap<String, DEPENDENCY_SPEC>>()

    fun mod(modId: String, action: MOD_SPEC.() -> Unit) {
        val mod = mods.getOrPut(modId) { createModEntrySpec() }
        mod.action()
    }

    fun dependency(modId: String, depId: String, action: DEPENDENCY_SPEC.() -> Unit) {
        val deps = dependencies.getOrPut(modId) { mutableMapOf() }
        deps[depId] = createDependencyEntrySpec().apply(action)
    }

    protected abstract fun createModEntrySpec(): MOD_SPEC
    protected abstract fun createDependencyEntrySpec(): DEPENDENCY_SPEC

    internal open fun toModel(): FmlModsToml = FmlModsToml(
        modLoader = modLoader.get(),
        loaderVersion = loaderVersion.get(),
        issueTrackerURL = issueTrackerURL.orNull,
        license = license.get(),
        dependencies = dependencies.takeUnless { it.isEmpty() }?.mapValues { (_, deps) ->
            deps.toModel()
        },
        mods = mods.map { (modId, spec) ->
            spec.toModel(modId)
        },
    )

    private fun MutableMap<String, DEPENDENCY_SPEC>.toModel(): List<FmlDependencyEntry> =
        map { (depId, spec) ->
            spec.toModel(depId)
        }
}

sealed class ModEntrySpec {
    @get:Input abstract val version: Property<String>
    @get:Input abstract val displayName: Property<String>
    @get:Input @get:Optional abstract val description: Property<String>
    @get:Input @get:Optional abstract val logoFile: Property<String>
    @get:Input @get:Optional abstract val logoBlur: Property<Boolean>
    @get:Input @get:Optional abstract val authors: Property<String>
    @get:Input @get:Optional abstract val credits: Property<String>
    @get:Input @get:Optional abstract val displayURL: Property<String>
    @get:Input @get:Optional abstract val updateJSONURL: Property<String>

    internal open fun toModel(modId: String) = FmlModEntry(
        modId = modId,
        version = version.get(),
        displayName = displayName.get(),
        updateJSONURL = updateJSONURL.orNull,
        displayURL = displayURL.orNull,
        logoFile = logoFile.orNull,
        logoBlur = logoBlur.orNull,
        credits = credits.orNull,
        authors = authors.orNull,
        description = description.orNull?.let { it.trimEnd() + "\n" },
    )
}

sealed class DependencyEntrySpec {
    @get:Input abstract val versionRange: Property<String>
    @get:Input @get:Optional abstract val ordering: Property<String>
    @get:Input @get:Optional abstract val side: Property<String>

    internal open fun toModel(id: String) = FmlDependencyEntry(
        modId = id,
        versionRange = versionRange.get(),
        ordering = ordering.orNull,
        side = side.orNull,
    )
}
