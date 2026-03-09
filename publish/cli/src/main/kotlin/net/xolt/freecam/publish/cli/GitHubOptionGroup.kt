package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import net.xolt.freecam.publish.model.GitHubConfig

internal class GitHubOptionGroup : GitHubConfig, OptionGroup(
    name = "GitHub options",
    help = "Options for configuring GitHub release publishing",
) {
    override val token: String by option("--gh-token", envvar = "GITHUB_TOKEN")
        .help("GitHub token to use for authenticating requests")
        .required()
        .validate {
            require(it.isNotBlank()) { "GitHub token cannot be blank" }
        }

    override val owner: String by option("--gh-owner", envvar = "GITHUB_REPOSITORY_OWNER")
        .help("GitHub repository owner")
        .required()
        .validate {
            require(it.isNotBlank()) { "is blank" }
            require(!it.contains('/')) { "contains '/'" }
        }

    override val repo: String by option("--gh-repo", envvar = "GITHUB_REPOSITORY")
        .help("GitHub repository name. When GITHUB_REPOSITORY is used, the first segment is discarded ('owner/repo' → 'repo')")
        .convert {
            when (name) {
                "GITHUB_REPOSITORY" -> it.substringAfter('/')
                else -> it
            }
        }
        .required()
        .validate {
            require(it.isNotBlank()) { "is blank" }
            require(!it.contains('/')) { "contains '/'" }
        }

    override val headSha: String by option("--git-sha", envvar = "GITHUB_SHA")
        .help("Git SHA to associate with the release")
        .required()
        .validate {
            require(it.isNotBlank()) { "Git SHA cannot be blank" }
        }

    override fun toString(): String {
        val maskedToken = token.take(4).padEnd(10, '*')
        return "GitHubOptionGroup(token='$maskedToken', owner='$owner', repo='$repo', headSha='$headSha')"
    }
}