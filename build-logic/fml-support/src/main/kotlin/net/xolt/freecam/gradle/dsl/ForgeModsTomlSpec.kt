package net.xolt.freecam.gradle.dsl

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class ForgeModsTomlSpec @Inject constructor(objects: ObjectFactory) : ModsTomlSpec<ForgeModEntrySpec, ForgeDependencyEntrySpec>(objects) {
    final override fun createModEntrySpec() = objects.newInstance<ForgeModEntrySpec>()
    final override fun createDependencyEntrySpec() = objects.newInstance<ForgeDependencyEntrySpec>()
}

abstract class ForgeModEntrySpec : ModEntrySpec()

abstract class ForgeDependencyEntrySpec @Inject constructor(objects: ObjectFactory) : DependencyEntrySpec() {
    @get:Input val mandatory = objects.property<Boolean>().convention(true)
    override fun toModel(id: String) = super.toModel(id).copy(mandatory = mandatory.get())
}
