package net.xolt.freecam.publish.http

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.measureTime
import kotlin.time.times

private const val TEST_URL = "https://example.com"

class GitHubClientConfigTest {

    @Test
    fun `retries on IOException`() = runTest {
        val attempts = countRequestAttempts(
            clientFactory = { testClient(engine = it, maxRetries = 2) },
            handler = { throw IOException("network error") },
        ) {
            shouldThrow<IOException> { get(TEST_URL) }
        }

        attempts shouldBe 3
    }

    @Test
    fun `retries on 503`() = runTest {
        val attempts = countRequestAttempts(
            clientFactory = { testClient(engine = it, maxRetries = 2) },
            handler = { respond("", HttpStatusCode.ServiceUnavailable) }
        ) {
            shouldThrow<ResponseException> { get(TEST_URL) }
        }

        attempts shouldBe 3
    }

    @Test
    fun `retries using RetryAfter header`() = runTest {
        val retries = 2

        // Large value so it overrides the default delay strategy.
        // TestCoroutineScheduler skips delays, so tests stay fast.
        val delay = 2.minutes

        listOf(
            HttpStatusCode.Forbidden,
            HttpStatusCode.TooManyRequests,
        ).forEach { status ->
            var duration = Duration.ZERO

            val attempts = countRequestAttempts(
                clientFactory = { testClient(engine = it, maxRetries = retries) },
                handler = {
                    respond("", status, buildHeaders { appendRetryAfter(delay) })
                }
            ) {
                duration = testScheduler.timeSource.measureTime {
                    shouldThrow<ResponseException> { get(TEST_URL) }
                }
            }

            attempts shouldBe retries + 1
            duration shouldBe retries * delay
        }
    }

    @Test
    fun `handles GitHub error response`() = runTest {
        val engine = MockEngine {
            respond(
                content = """
                    {
                      "message": "Validation Failed",
                      "errors": [{"resource":"Release","field":"tag_name","code":"already_exists"}]
                    }
                """.trimIndent(),
                status = HttpStatusCode.UnprocessableEntity,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = testClient(engine, maxRetries = 2)

        val ex = shouldThrow<GitHubResponseException> { client.get(TEST_URL) }
        ex.message shouldStartWith "GitHub API error: ${HttpStatusCode.UnprocessableEntity} - Validation Failed"
        ex.message shouldContain "already_exists"
    }
}

private fun testClient(
    engine: HttpClientEngine,
    token: String = "token",
    maxRetries: Int = 4,
    extraConfig: HttpClientConfig<*>.() -> Unit = { },
) = HttpClient(engine) {
    configureGitHubClient(
        token = token,
        retryExceptions = maxRetries,
        retryHttpErrors = maxRetries,
    )
    extraConfig()
}

private suspend fun countRequestAttempts(
    clientFactory: (HttpClientEngine) -> HttpClient,
    handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    block: suspend HttpClient.() -> Unit
): Int {
    var count = 0

    val engine = MockEngine { request ->
        count++
        handler(request)
    }

    clientFactory(engine).block()

    return count
}

private fun HeadersBuilder.appendRetryAfter(delay: Duration) {
    append(HttpHeaders.RetryAfter, delay.inWholeSeconds.toString())
}
