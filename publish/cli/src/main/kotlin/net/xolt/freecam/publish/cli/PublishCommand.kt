package net.xolt.freecam.publish.cli

import com.github.ajalt.clikt.command.main
import net.xolt.freecam.model.ReleaseMetadata
import net.xolt.freecam.publish.PublisherFactory

@JvmInline
value class PublishCommand internal constructor(
    private val cmd: PublishCliCommand
) {

    constructor(
        metadataSupplier: () -> ReleaseMetadata,
        publisherFactory: PublisherFactory,
    ) : this(PublishCliCommand(
        metadataSupplier = metadataSupplier,
        publisherFactory = publisherFactory,
    ))

    suspend fun main(args: Array<String>) = cmd.main(args)
}