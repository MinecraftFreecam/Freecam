package net.xolt.freecam.publish.model

interface GitHubConfig {
    val token: String
    val owner: String
    val repo: String
    val headSha: String
}