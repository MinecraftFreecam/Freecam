package net.xolt.freecam.publish.http

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubErrorResponse(
    val message: String,
    val errors: List<GitHubErrorDetail>? = null,
    @SerialName("documentation_url")
    val documentationUrl: String? = null,
)

@Serializable
data class GitHubErrorDetail(
    val resource: String? = null,
    val field: String? = null,
    val code: String? = null,
)

val GitHubErrorDetail.path: String
    get() = listOfNotNull(resource, field).joinToString(".")

suspend fun ResponseException.toGitHubResponseException() =
    response.body<GitHubErrorResponse>().toException(this)

suspend fun ResponseException.toGitHubResponseExceptionOrNull(): GitHubResponseException? {
    return try {
        toGitHubResponseException()
    } catch (_: NoTransformationFoundException) {
        null
    }
}

fun GitHubErrorResponse.toException(cause: ResponseException) =
    GitHubResponseException(this, cause)

class GitHubResponseException(
    val error: GitHubErrorResponse,
    override val cause: ResponseException,
) : RuntimeException(
    buildString {
        append("GitHub API error: ${cause.response.status} - ${error.message}")
        error.errors?.forEach { append("\n  • ${it.path}: ${it.code}") }
    },
    cause,
)