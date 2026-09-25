package net.xolt.freecam.network

import io.kotest.matchers.shouldBe
import net.xolt.freecam.config.load.BasicConfigLoader
import net.xolt.freecam.config.load.RawJsonPreservingSerializer
import net.xolt.freecam.config.model.ModConfigDTO
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.test.Test

class HostedServerPolicyTest {
    private val hostedPolicy = HostedServerPolicy()

    @Test
    fun `saved client configuration can be loaded as a server policy`() {
        val dir = createTempDirectory("freecam-server-policy").apply {
            toFile().deleteOnExit()
        }
        val file = dir.resolve("freecam.json")
        val loader = BasicConfigLoader(RawJsonPreservingSerializer(), ModConfigDTO::class.java, file)
        val config = ModConfigDTO()
        config.serverPolicy.collision.allowIgnoring = false
        config.serverPolicy.allowCameraInteractions = false
        loader.write(config)
        val saved = file.readText()
        val serverConfig = loader.read()
        ServerPolicy.create(serverConfig.serverPolicy) shouldBe ServerPolicy(true, ServerPolicy.CollisionPolicy(false), true, false)

        hostedPolicy.configure(serverConfig)
        ServerPolicies().applyJson("""{"allowFreecam":false,"allowFullbright":false}""") shouldBe true
        hostedPolicy.forClients() shouldBe ServerPolicy(true, ServerPolicy.CollisionPolicy(false), true, false)
        loader.write(serverConfig)
        file.readText() shouldBe saved
    }

    @Test
    fun `host is only restricted when applyToHost is enabled`() {
        val config = ModConfigDTO()
        config.serverPolicy.allowFreecam = false
        hostedPolicy.configure(config)
        hostedPolicy.forHost() shouldBe ServerPolicy.ALLOW_ALL

        config.serverPolicy.applyToHost = true
        hostedPolicy.configure(config)
        hostedPolicy.forHost() shouldBe hostedPolicy.forClients()
    }

    @Test
    fun `published snapshots only change when settings are saved`() {
        val config = ModConfigDTO()
        hostedPolicy.configure(config)
        config.serverPolicy.allowFreecam = false
        hostedPolicy.forClients() shouldBe ServerPolicy.ALLOW_ALL
        hostedPolicy.configure(config)
        hostedPolicy.forClients() shouldBe ServerPolicy(false, ServerPolicy.CollisionPolicy(true), true, true)
    }
}
