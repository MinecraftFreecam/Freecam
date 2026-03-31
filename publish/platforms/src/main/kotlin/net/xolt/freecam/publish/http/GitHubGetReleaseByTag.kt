package net.xolt.freecam.publish.http

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
class GitHubGetReleaseByTag private constructor(
    @Required
    override val variables: Variables,
) : GraphQLClientRequest<GitHubGetReleaseByTag.Result> {

    constructor(owner: String, repo: String, tag: String) : this(
        variables = Variables(owner, repo, tag),
    )

    override val operationName = "GetReleaseByTag"

    @Required
    override val query = $$"""
        query($owner: String!, $repo: String!, $tag: String!) {
          repository(owner: $owner, name: $repo) {
            release(tagName: $tag) {
              databaseId
            }
          }
        }
    """.trimIndent()

    override fun responseType() = Result::class

    @Serializable
    data class Variables(
        val owner: String,
        val repo: String,
        val tag: String,
    )

    @Serializable
    data class Result(val repository: Repository?)

    @Serializable
    data class Repository(val release: Release?)

    @Serializable
    data class Release(val databaseId: Long)
}