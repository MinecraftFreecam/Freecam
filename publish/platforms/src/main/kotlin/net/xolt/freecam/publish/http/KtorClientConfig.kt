package net.xolt.freecam.publish.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import net.xolt.freecam.publish.logging.LogLevel
import net.xolt.freecam.publish.logging.useLoggingAdapter
import kotlin.time.Duration.Companion.seconds

internal fun HttpClientConfig<*>.commonConfig(
    logScope: String,
    logLevel: LogLevel = LogLevel.DEBUG,
    retryExceptions: Int = 4,
    retryHttpErrors: Int = 4,
) {
    install(Logging) {
        useLoggingAdapter(logScope, logLevel)
    }
    install(UserAgent) {
        agent = "Freecam Publishing"
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30.seconds.inWholeMilliseconds
        connectTimeoutMillis = 10.seconds.inWholeMilliseconds
        socketTimeoutMillis = 30.seconds.inWholeMilliseconds
    }
    install(HttpRequestRetry) {
        retryOnException(maxRetries = retryExceptions)
        retryOnRateLimitsOrServerErrors(maxRetries = retryHttpErrors)
        exponentialDelay(respectRetryAfterHeader = true)
        modifyRequest { request ->
            request.headers.append(
                name = HttpHeaders.XRetryCount,
                value = retryCount.toString(),
            )
        }
    }
    expectSuccess = true
}

fun HttpRequestRetryConfig.retryOnRateLimitsOrServerErrors(maxRetries: Int = 4) {
    retryIf(maxRetries) { _, response ->
        when (response.status) {
            // 429 TooManyRequests is usually caused by ratelimits
            HttpStatusCode.TooManyRequests -> true

            // 403 Forbidden is sometimes annotated with RetryAfter
            HttpStatusCode.Forbidden ->
                HttpHeaders.RetryAfter in response.headers
                        || response.headers[HttpHeaders.XRateLimitRemaining] == "0"

            // 5xx server errors are usually transient
            else -> response.status.value in 500..599
        }
    }
}
