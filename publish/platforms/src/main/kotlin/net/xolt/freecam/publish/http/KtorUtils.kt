package net.xolt.freecam.publish.http

import io.ktor.client.*
import io.ktor.http.*

typealias HttpClientFactory = (HttpClientConfig<*>.() -> Unit) -> HttpClient

val ContentType.Application.GitHubJson
    get() = ContentType("application", "vnd.github+json")

val ContentType.Application.JavaArchive
    get() = ContentType("application", "java-archive")

val HttpHeaders.XRetryCount
    get() = "X-Retry-Count"

val HttpHeaders.XRateLimitRemaining
    get() = "X-RateLimit-Remaining"
