package net.xolt.freecam.gradle

import org.gradle.api.provider.ProviderFactory
import org.gradle.process.ProcessExecutionException
import java.io.File

internal fun interface CommandRunner {
    fun run(directory: File, vararg args: String): Result?

    operator fun invoke(directory: File, vararg args: String): Result? =
        run(directory, *args)

    data class Result(val exitCode: Int, val stdout: String?, val stderr: String?)
}

internal class GradleCommandRunner(private val providers: ProviderFactory) : CommandRunner {
    override fun run(directory: File, vararg args: String): CommandRunner.Result? = try {
        val exec = providers.exec {
            workingDir(directory)
            commandLine(*args)
            isIgnoreExitValue = true
        }
        CommandRunner.Result(
            exitCode = exec.result.get().exitValue,
            stdout = exec.standardOutput.asText.orNull,
            stderr = exec.standardError.asText.orNull,
        )
    } catch (_: ProcessExecutionException) {
        // ProcessExecutionException occurs when the command can't be executed,
        // typically because git isn't on the PATH
        null
    }
}
