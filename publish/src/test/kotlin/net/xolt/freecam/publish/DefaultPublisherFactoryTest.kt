package net.xolt.freecam.publish

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.xolt.freecam.publish.model.GitHubConfig
import net.xolt.freecam.publish.platforms.GitHubPlatform
import net.xolt.freecam.publish.platforms.Platform
import net.xolt.freecam.test.createTestDir
import kotlin.test.Test

class DefaultPublisherFactoryTest {

    private data class DummyConfig(
        override val token: String = "token",
        override val owner: String = "owner",
        override val repo: String = "repo",
        override val headSha: String = "sha",
    ) : GitHubConfig

    @Test
    fun `uses expected platform impls`() {
        assertSoftly(listOf(
            "Default" to false,
            "DryRun" to true,
        )) {
            forEach { (prefix, dryRun) ->
                val publisher = DefaultPublisherFactory.create(
                    dryRun = dryRun,
                    artifactsDir = createTestDir(),
                    githubConfig = DummyConfig(),
                )

                publisher.shouldBeInstanceOf<DefaultPublisher>()
                publisher.github.shouldBeImplOf<GitHubPlatform>(prefix)
            }
        }
    }

    private inline fun <reified T : Platform> Platform.shouldBeImplOf(prefix: String) {
        shouldBeInstanceOf<T>()
        javaClass.simpleName shouldBe "$prefix${T::class.simpleName}"
    }
}
