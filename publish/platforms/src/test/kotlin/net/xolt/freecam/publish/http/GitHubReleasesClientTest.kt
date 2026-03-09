package net.xolt.freecam.publish.http

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.xolt.freecam.publish.http.GitHubReleaseDTOFixtures.testAsset
import net.xolt.freecam.publish.http.GitHubReleaseDTOFixtures.testRelease
import net.xolt.freecam.publish.model.GitHubConfig
import kotlin.test.Test

class GitHubReleasesClientTest {

    private data class TestConfig(
        override val owner: String = "o",
        override val repo: String = "r",
        override val token: String = "t",
        override val headSha: String = "abc123",
    ): GitHubConfig

    @Test
    fun `getReleaseIDByTag sends correct graphql request`() = runTest {
        val tag = "v1.2.3"
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respondOk("""
                {
                  "data": {
                    "repository": {
                      "release": { "databaseId": 42 }
                    }
                  }
                }
            """.trimIndent())
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        val result = client.getReleaseIDByTag(tag)
        val body = captured.body.toByteArray().decodeToString()

        withClue("got expected result") { result shouldBe 42 }

        withClue("used expected endpoint") {
            captured.method shouldBe HttpMethod.Post
            captured.url shouldBe Url("https://api.github.com/graphql")
        }

        withClue("body contains query and variables") {
            body shouldContain "\"query\""
            body shouldContain "\"variables\""
        }

        withClue("body matches serialized GitHubGetReleaseByTag request") {
            body shouldBe Json.encodeToString(
                GitHubGetReleaseByTag(
                    owner = config.owner,
                    repo = config.repo,
                    tag = tag,
                )
            )
        }
    }

    @Test
    fun `createRelease sends correct request`() = runTest {
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respond(
                status = HttpStatusCode.Created,
                content = Json.encodeToString(testRelease()),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        client.createRelease(
            tagName = "v1",
            targetCommitish = "abc123",
            name = "Release 1",
            body = "notes",
            prerelease = true,
            draft = false,
        )

        val bodyStr = captured.body.toByteArray().decodeToString()
        val body = Json.decodeFromString<GitHubReleaseRequest>(bodyStr)

        captured.method shouldBe HttpMethod.Post
        captured.url shouldBe Url("https://api.github.com/repos/${config.owner}/${config.repo}/releases")
        body shouldBe GitHubReleaseRequest(
            tagName = "v1",
            targetCommitish = "abc123",
            name = "Release 1",
            body = "notes",
            prerelease = true,
            draft = false,
        )
    }

    @Test
    fun `getReleaseIDByTag returns null when missing`() = runTest {
        val config = TestConfig()
        val engine = MockEngine {
            respondOk("""{"data":{"repository":{"release":null}}}""")
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        val result = client.getReleaseIDByTag("v1")

        result.shouldBeNull()
    }

    @Test
    fun `getReleaseIDByTag throws on graphql error`() = runTest {
        val config = TestConfig()

        val engine = MockEngine {
            respondOk("""{"errors":[{"message": "bad"}]}""")
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        val ex = shouldThrow<GraphQLClientException> {
            client.getReleaseIDByTag("v1")
        }

        ex.message shouldContain "bad"
    }

    @Test
    fun `updateRelease with id appends id to base path`() = runTest {
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respond(
                content = Json.encodeToString(testRelease()),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        client.updateRelease(
            releaseId = 123,
            name = "new",
        )

        captured.method shouldBe HttpMethod.Patch
        captured.url shouldBe Url("https://api.github.com/repos/o/r/releases/123")
    }

    @Test
    fun `updateRelease with release uses release url`() = runTest {
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respond(
                content = Json.encodeToString(testRelease()),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        client.updateRelease(
            release = testRelease(url = "https://api.example.com/custom/1"),
            name = "new",
        )

        captured.url shouldBe Url("https://api.example.com/custom/1")
    }

    @Test
    fun `updateDraftState sends only draft`() = runTest {
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respond(
                content = Json.encodeToString(testRelease()),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        client.updateDraftState(123, draft = true)

        val bodyStr = captured.body.toByteArray().decodeToString()
        val body = Json.decodeFromString<GitHubReleaseRequest>(bodyStr)

        body.draft.shouldBeTrue()
        body.tagName.shouldBeNull()
        bodyStr shouldNotContain "tag_name"
    }

    @Test
    fun `uploadAssetToRelease sends expected input`() = runTest {
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respond(
                content = Json.encodeToString(testAsset()),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        client.uploadAssetToRelease(
            release = testRelease(uploadUrl = "https://upload"),
            name = "file.jar",
            contentType = ContentType.Application.JavaArchive,
            content = byteArrayOf(1, 2, 3),
        )

        captured.method shouldBe HttpMethod.Post
        captured.url shouldBe Url("https://upload?name=file.jar")
        captured.headers[HttpHeaders.Authorization] shouldBe "Bearer ${config.token}"

        val body = captured.body
        body.shouldBeInstanceOf<OutgoingContent.ByteArrayContent>()
        body.contentType shouldBe ContentType.Application.JavaArchive
        body.bytes() shouldBe byteArrayOf(1, 2, 3)
    }

    @Test
    fun `deleteAsset calls delete on asset url`() = runTest {
        val config = TestConfig()
        lateinit var captured: HttpRequestData

        val engine = MockEngine { request ->
            captured = request
            respond(status = HttpStatusCode.NoContent, content = byteArrayOf())
        }

        val client = GitHubReleasesClient(config, httpEngine = engine)

        client.deleteAsset(testAsset(url = "https://delete/me"))

        captured.method shouldBe HttpMethod.Delete
        captured.url shouldBe Url("https://delete/me")
    }
}