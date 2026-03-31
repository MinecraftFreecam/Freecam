package net.xolt.freecam.publish.http

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.xolt.freecam.publish.model.GitHubConfig

class GitHubReleasesClient private constructor(
    private val config: GitHubConfig,
    httpClientFactory: HttpClientFactory,
) : AutoCloseable {

    constructor(
        config: GitHubConfig,
        httpEngine: HttpClientEngine,
        httpConfig: HttpClientConfig<*>.() -> Unit = {},
    ) : this(
        config = config,
        httpClientFactory = { privateConfig ->
            HttpClient(httpEngine) {
                privateConfig()
                httpConfig()
            }
        },
    )

    constructor(
        config: GitHubConfig,
        httpEngineFactory: HttpClientEngineFactory<*> = CIO,
        httpConfig: HttpClientConfig<*>.() -> Unit = {},
    ) : this(
        config = config,
        httpClientFactory = { privateConfig ->
            HttpClient(httpEngineFactory) {
                privateConfig()
                httpConfig()
            }
        }
    )

    private val endpoint = githubUrl("repos", config.owner, config.repo, "releases")

    private val http = httpClientFactory {
        configureGitHubClient(token = config.token)
    }

    val graphql = GraphQLKtorClient(
        url = githubUrl("graphql").toURI().toURL(),
        httpClient = http,
    )

    suspend fun getReleaseIDByTag(tag: String): Long? =
        graphql.execute(
            GitHubGetReleaseByTag(
                owner = config.owner,
                repo = config.repo,
                tag = tag,
            )
        ).dataOrThrow().repository?.release?.databaseId

    suspend fun createRelease(
        tagName: String,
        targetCommitish: String? = null,
        name: String,
        body: String,
        prerelease: Boolean = false,
        draft: Boolean = false,
    ): GitHubRelease =
        http.post {
            url(endpoint)
            contentType(ContentType.Application.Json)
            setBody(GitHubReleaseRequest(
                tagName = tagName,
                targetCommitish = targetCommitish,
                name = name,
                body = body,
                prerelease = prerelease,
                draft = draft
            ))
        }.body()

    suspend fun updateRelease(
        release: GitHubRelease,
        tagName: String? = null,
        targetCommitish: String? = null,
        name: String? = null,
        body: String? = null,
        prerelease: Boolean? = null,
    ) = updateRelease(
        tagName = tagName,
        targetCommitish = targetCommitish,
        name = name,
        body = body,
        prerelease = prerelease,
    ) {
        takeFrom(release.url)
    }

    suspend fun updateRelease(
        releaseId: Long,
        tagName: String? = null,
        targetCommitish: String? = null,
        name: String? = null,
        body: String? = null,
        prerelease: Boolean? = null,
    ) = updateRelease(
        tagName = tagName,
        targetCommitish = targetCommitish,
        name = name,
        body = body,
        prerelease = prerelease,
    ) {
        takeFrom(endpoint)
        appendPathSegments(releaseId.toString())
    }

    private suspend fun updateRelease(
        tagName: String?,
        targetCommitish: String?,
        name: String?,
        body: String?,
        prerelease: Boolean?,
        urlConfig: URLBuilder.(URLBuilder) -> Unit,
    ): GitHubRelease =
        http.patch {
            url(urlConfig)
            contentType(ContentType.Application.Json)
            setBody(GitHubReleaseRequest(
                tagName = tagName,
                targetCommitish = targetCommitish,
                name = name,
                body = body,
                prerelease = prerelease,
            ))
        }.body()

    suspend fun updateDraftState(release: GitHubRelease, draft: Boolean): GitHubRelease =
        updateDraftState(draft = draft) {
            takeFrom(release.url)
        }

    suspend fun updateDraftState(releaseId: Long, draft: Boolean): GitHubRelease =
        updateDraftState(draft = draft) {
            takeFrom(endpoint)
            appendPathSegments(releaseId.toString())
        }

    private suspend fun updateDraftState(
        draft: Boolean,
        urlConfig: URLBuilder.(URLBuilder) -> Unit,
    ): GitHubRelease =
        http.patch {
            url(urlConfig)
            contentType(ContentType.Application.Json)
            setBody(GitHubReleaseRequest(draft = draft))
        }.body()

    suspend fun deleteAsset(asset: GitHubReleaseAsset) {
        http.delete(asset.url)
    }

    suspend fun uploadAssetToRelease(
        release: GitHubRelease,
        name: String,
        label: String? = null,
        contentType: ContentType,
        content: ByteArray,
    ): GitHubReleaseAsset =
        http.post(release.uploadUrl) {
            url {
                parameters.append("name", name)
                label?.let { parameters.append("label", it) }
            }
            contentType(contentType)
            setBody(content)
        }.body()

    override fun close() = http.close()
}
