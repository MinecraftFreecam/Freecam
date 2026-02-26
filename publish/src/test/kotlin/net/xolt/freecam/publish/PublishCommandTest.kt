package net.xolt.freecam.publish

import io.kotest.assertions.withClue
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.xolt.freecam.publish.cli.PublishCommand
import net.xolt.freecam.test.createTestDir
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.test.Test

class PublishCommandTest {

    @Test
    fun `command accepts generated metadata`() = runTest {
        val publisher = mockk<Publisher>(relaxUnitFun = true)
        val publisherFactory = object : PublisherFactory {
            override fun create(
                dryRun: Boolean,
                artifactsDir: Path,
            ) = publisher
        }

        withClue("Running command should not throw") {
            PublishCommand(
                metadataSupplier = ::loadMetadata,
                publisherFactory = publisherFactory,
            ).main(arrayOf(createTestDir().pathString))
        }

        withClue("Command should publish with expected metadata") {
            coVerify { publisher.publish(loadMetadata()) }
        }
    }
}