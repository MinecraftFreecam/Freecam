package net.xolt.freecam.publish.http

import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientResponse

data class GraphQLClientException(val errors: Iterable<GraphQLClientError>)
    : RuntimeException(errors.joinToString("; "))

internal fun <T> GraphQLClientResponse<T>.dataOrThrow(): T =
    errors
        .takeUnless { it.isNullOrEmpty() }
        ?.let { throw GraphQLClientException(it) }
        ?: data!!