package net.xolt.freecam.gradle

import java.io.File

internal data class GitMetadata(
    val revision: String,
    val isDirty: Boolean,
)

internal class GitRepository(
    private val projectDir: File,
    private val cmd: CommandRunner,
) {
    /**
     * @return [Result] containing [GitMetadata] for success, or [Result.Failure] for expected errors
     * @throws IllegalStateException for unexpected errors
     */
    fun resolveMetadata(): Result<GitMetadata> {
        // First, check we are "in" the right git repo
        cmd(projectDir, "git", "rev-parse", "--show-toplevel")?.apply {
            if (exitCode == 128 && stderr?.startsWith("fatal: not a git repository") == true) {
                return Result.failure(IllegalStateException("Not a git repository"))
            }

            if (exitCode != 0) error("Unknown git error ($exitCode): $stderr")

            val workTree = stdout?.trim()?.let(::File)
                ?: error("Error checking git toplevel")
            if (projectDir.canonicalFile != workTree.canonicalFile) {
                return Result.failure(IllegalStateException("Git repo ($workTree) is not project ($projectDir)."))
            }
        } ?: return Result.failure(IllegalStateException("Could not execute git"))

        val head = cmd(projectDir, "git", "rev-parse", "--short", "HEAD")?.stdout?.trim()
            ?: error("Error parsing git HEAD of $projectDir")

        val status = cmd(projectDir, "git", "status", "--porcelain")?.stdout?.trim()
            ?: error("Error getting git status for $projectDir")

        return Result.success(GitMetadata(
            revision = head,
            isDirty = status.isNotBlank(),
        ))
    }
}
