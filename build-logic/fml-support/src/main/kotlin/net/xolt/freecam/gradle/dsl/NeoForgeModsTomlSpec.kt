package net.xolt.freecam.gradle.dsl

import net.xolt.freecam.model.FmlAccessTransformerEntry
import net.xolt.freecam.model.FmlDependencyType
import net.xolt.freecam.model.FmlMixinEntry
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class NeoForgeModsTomlSpec @Inject constructor(objects: ObjectFactory) : ModsTomlSpec<NeoForgeModEntrySpec, NeoForgeDependencyEntrySpec>(objects) {

    @get:Input
    @get:Optional
    abstract val accessTransformers: SetProperty<String>

    @get:Nested
    val mixins = mutableMapOf<String, NeoForgeMixinEntrySpec>()

    fun accessTransformer(config: String) = accessTransformers.add(config)

    fun mixin(config: String, action: NeoForgeMixinEntrySpec.() -> Unit = { }) {
        val spec = mixins.getOrPut(config) { createMixinEntrySpec() }
        spec.action()
    }

    final override fun createModEntrySpec() = objects.newInstance<NeoForgeModEntrySpec>()
    final override fun createDependencyEntrySpec() = objects.newInstance<NeoForgeDependencyEntrySpec>()
    protected fun createMixinEntrySpec() = objects.newInstance<NeoForgeMixinEntrySpec>()

    override fun toModel() = super.toModel().copy(
        accessTransformers = accessTransformers.orNull
            ?.takeUnless { it.isEmpty() }
            ?.map(::FmlAccessTransformerEntry),
        mixins = mixins.takeUnless { it.isEmpty() }?.map { (config, spec) ->
            FmlMixinEntry(
                config = config,
                requiredMods = spec.requiredMods.orNull
                    ?.takeUnless { it.isEmpty() }
                    ?.toList(),
            )
        },
    )
}

abstract class NeoForgeModEntrySpec : ModEntrySpec()

abstract class NeoForgeDependencyEntrySpec @Inject constructor(objects: ObjectFactory) : DependencyEntrySpec() {
    @get:Input val type = objects.property<FmlDependencyType>().convention(FmlDependencyType.REQUIRED)
    override fun toModel(id: String) = super.toModel(id).copy(type = type.get())
}

abstract class NeoForgeMixinEntrySpec {
    @get:Input
    @get:Optional
    abstract val requiredMods: SetProperty<String>
}
