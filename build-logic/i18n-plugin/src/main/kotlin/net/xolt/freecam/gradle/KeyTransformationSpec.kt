package net.xolt.freecam.gradle

import net.xolt.freecam.i18n.KeyTransformation
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class KeyTransformationSpec @Inject constructor(
    @get:Input
    val name: String,
    objects: ObjectFactory,
) {
    @get:Input
    val keepOriginal: Property<Boolean> = objects.property<Boolean>().apply {
        finalizeValueOnRead()
        convention(false)
    }

    @get:Input
    abstract val names: ListProperty<String>

    fun rename(provider: Provider<String>) {
        names.add(provider)
    }

    fun rename(vararg name: String) {
        names.addAll(*name)
    }

    fun rename(names: Collection<String>) {
        this.names.addAll(names)
    }

    internal val asKeyTransformation: KeyTransformation
        get() = KeyTransformation(
            name = name,
            names = names.get(),
            keepOriginal = keepOriginal.get(),
        )
}
