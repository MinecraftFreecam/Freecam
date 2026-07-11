import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import dev.kikugie.stonecutter.data.tree.ProjectNode
import net.xolt.freecam.model.ModMetadata
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Alias for [`meta`][ModMetadata], as seen outside build-logic.
 */
internal val Project.meta get() = extensions.getByType<ModMetadata>()

/**
 * The `stonecutter` extension on :loader:version projects is a [StonecutterBuildExtension].
 * This binding provides access within build-logic.
 */
internal val Project.stonecutter get() = extensions.getByType<StonecutterBuildExtension>()

/**
 * The `stonecutter` extension in `stonecutter.gradle.kts` is a [StonecutterControllerExtension].
 * This binding provides access within build-logic.
 */
internal val Project.stonecutterController get() = extensions.getByType<StonecutterControllerExtension>()

/**
 * The stonecutter [ProjectNode] for the current version's `:common` project, e.g. `:common:1.12.11`.
 */
val Project.commonNode: ProjectNode get() = requireNotNull(stonecutter.node.sibling("common")) {
    "No common project for $project"
}
