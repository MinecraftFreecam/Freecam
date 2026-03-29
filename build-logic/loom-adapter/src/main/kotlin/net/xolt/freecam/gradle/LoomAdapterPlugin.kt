package net.xolt.freecam.gradle

import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import net.fabricmc.loom.LoomNoRemapGradlePlugin
import net.fabricmc.loom.LoomRemapGradlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import net.fabricmc.loom.util.Constants.Configurations as LoomConfigurations

/**
 * Abstract the differences between the old [LoomRemapGradlePlugin] and the new [LoomNoRemapGradlePlugin],
 * which removed obfuscation remapping features because Minecraft is no longer obfuscated, as of `26.1-snapshot-1`.
 *
 * @author KikuGie
 * @author Matt Sturgeon
 * @see <a href="https://discord.com/channels/1135884510613995590/1192572106056142951/1485634997837893653">original implementation</a>
 */
abstract class LoomAdapterPlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "loomAdapter"
    }

    override fun apply(target: Project): Unit = with(target) {
        val extension: LoomAdapterExtension
        val sc = extensions.getByType<StonecutterBuildExtension>()
        if (sc.current.parsed < "26.0") {
            setupRemappingLoom()
            extension = RemapLoomAdapterExtension(project)
        } else {
            setupLoomFacade()
            extension = NoRemapLoomAdapterExtension(project)
        }
        extensions.add<LoomAdapterExtension>(EXTENSION_NAME, extension)
    }

    private fun Project.setupRemappingLoom() {
        plugins.apply(LoomRemapGradlePlugin.NAME)
    }

    private fun Project.setupLoomFacade() {
        plugins.apply(LoomNoRemapGradlePlugin.NAME)

        // Create `mod` passthrough configurations
        sequenceOf(
            JavaPlugin.API_CONFIGURATION_NAME,
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME,
            JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME,
            LoomConfigurations.LOCAL_RUNTIME,
        ).forEach { name ->
            val modConfiguration: Provider<Configuration> = configurations.register("mod" + name.uppercaseFirstChar())
            configurations.named(name) { extendsFrom(modConfiguration) }
        }

        // Create `mappings` stub configuration
        configurations.register(LoomConfigurations.MAPPINGS) {
            isCanBeResolved = false
            isCanBeConsumed = false
        }

        // Create `namedElements` passthrough configuration
        configurations.register(LoomConfigurations.NAMED_ELEMENTS) {
            extendsFrom(configurations.named(JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME));
            isCanBeResolved = false
            isCanBeConsumed = true
        }
    }
}
