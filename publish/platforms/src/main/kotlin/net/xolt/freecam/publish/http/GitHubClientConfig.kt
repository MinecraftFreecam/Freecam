package net.xolt.freecam.publish.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.xolt.freecam.publish.logging.LogLevel

internal fun HttpClientConfig<*>.configureGitHubClient(
    token: String,
    logLevel: LogLevel = LogLevel.DEBUG,
    retryExceptions: Int = 4,
    retryHttpErrors: Int = 4,
) {
    commonConfig(
        logScope = "GitHubAPI",
        logLevel = logLevel,
        retryExceptions = retryExceptions,
        retryHttpErrors = retryHttpErrors,
    )
    install(DefaultRequest) {
        bearerAuth(token)
        accept(ContentType.Application.GitHubJson)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            ignoreUnknownKeys = true
        })
    }
    HttpResponseValidator {
        handleGitHubErrors()
    }
}

internal fun HttpCallValidatorConfig.handleGitHubErrors() {
    handleResponseExceptionWithRequest { cause, _ ->
        (cause as? ResponseException)
            ?.toGitHubResponseExceptionOrNull()
            ?.let { throw it }
    }
}

internal fun githubUrl(vararg path: String) = buildUrl {
    protocol = URLProtocol.HTTPS
    host = "api.github.com"
    path(*path)
}