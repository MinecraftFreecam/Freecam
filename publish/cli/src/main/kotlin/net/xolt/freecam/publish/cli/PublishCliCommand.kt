package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.path
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.PublisherFactory
import net.xolt.freecam.publish.logging.*
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

internal class PublishCliCommand(
    version: String? = null,
    metadataSupplier: () -> ReleaseMetadata,
    publisherFactory: PublisherFactory,
) : SuspendingCliktCommand(name = "publish") {

    init {
        context {
            versionOption(version ?: metadata.modVersion)
            helpFormatter = {
                MordantHelpFormatter(
                    context = it,
                    requiredOptionMarker = null,
                    showDefaultValues = true,
                    showRequiredTag = true,
                )
            }
        }
    }

    private val publisher by lazy {
        publisherFactory.create(
            dryRun = dryRun,
            artifactsDir = artifactsDir,
        )
    }

    val metadata by lazy(metadataSupplier)

    val artifactsDir: Path by argument("artifacts-dir").path()
        .help("Directory containing the release artifacts")
        .validate {
            require(it.exists()) {
                "${it.absolute()} does not exist"
            }
            require(it.isDirectory()) {
                "${it.absolute()} is not a directory"
            }
        }

    val dryRun: Boolean by option("--dry-run").flag()
        .help("Perform a dry run without making any actual API calls")

    private val verbosity by VerbosityOptionGroup()
    val logLevel: LogLevel get() = verbosity.level

    val ghaAnnotations by option("--gha-output", envvar = "GITHUB_ACTIONS")
        .help("Format output using GitHub Actions annotations (::error::, ::warning::, etc)")
        .flag()

    override suspend fun run() {
        Logger.configure {
            threshold = logLevel
            renderer =
                if (ghaAnnotations) GHALogRenderer
                else MordantLogRenderer
            output = { message ->
                // Use Clikt's `echo` for system-specific line endings and Mordant rendering.
                echo(message, err = level.isStderr)
            }
        }

        publisher.use { it.publish(metadata) }
    }
}
