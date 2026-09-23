package net.xolt.freecam.network

import io.kotest.matchers.shouldBe
import net.xolt.freecam.config.load.BasicConfigLoader
import net.xolt.freecam.config.load.RawJsonPreservingSerializer
import net.xolt.freecam.config.model.ModConfigDTO
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.test.AfterTest
import kotlin.test.Test

class HostedServerPolicyTest {
    @AfterTest
    fun cleanup() {
        HostedServerPolicy.configure(ModConfigDTO())
        ServerPolicies.reset()
    }

    @Test
    fun `saved client configuration can be loaded as a server policy`() {
        val dir = createTempDirectory("freecam-server-policy").apply {
            toFile().deleteOnExit()
        }
        val file = dir.resolve("freecam.json")
        val loader = BasicConfigLoader(RawJsonPreservingSerializer(), ModConfigDTO::class.java, file)
        val config = ModConfigDTO()
        config.serverPolicy.collision.allowIgnoring = false
        config.serverPolicy.allowInteract = false
        loader.write(config)
        val saved = file.readText()
        val serverConfig = loader.read()
        serverConfig.serverPolicy.snapshot() shouldBe ServerPolicy(true, ServerPolicy.CollisionPolicy(false), true, false)

        HostedServerPolicy.configure(serverConfig)
        ServerPolicies.applyJson("""{"allowFreecam":false,"allowFullbright":false}""") shouldBe true
        HostedServerPolicy.get() shouldBe ServerPolicy(true, ServerPolicy.CollisionPolicy(false), true, false)
        loader.write(serverConfig)
        file.readText() shouldBe saved
    }

    @Test
    fun `published snapshots only change when settings are saved`() {
        val config = ModConfigDTO()
        HostedServerPolicy.configure(config)
        config.serverPolicy.allowFreecam = false
        HostedServerPolicy.get() shouldBe ServerPolicy.ALLOW_ALL
        HostedServerPolicy.configure(config)
        HostedServerPolicy.get() shouldBe ServerPolicy(false, ServerPolicy.CollisionPolicy(true), true, true)
    }
}
