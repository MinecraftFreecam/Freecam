package net.xolt.freecam.network

import io.kotest.matchers.shouldBe
import net.xolt.freecam.config.model.ServerPolicyConfig
import kotlin.test.Test

class ServerPolicyTest {
    @Test
    fun `create copies every configured policy`() {
        val config = ServerPolicyConfig()
        config.allowFreecam = false
        config.collision.allowIgnoring = false
        config.allowInteract = false
        ServerPolicy.create(config) shouldBe ServerPolicy(false, ServerPolicy.CollisionPolicy(false), true, false)
    }

    @Test
    fun `create allows everything for a missing config`() {
        ServerPolicy.create(null) shouldBe ServerPolicy.ALLOW_ALL
    }

    @Test
    fun `create allows ignoring collision for a missing collision section`() {
        val config = ServerPolicyConfig()
        config.collision = null
        ServerPolicy.create(config) shouldBe ServerPolicy.ALLOW_ALL
    }

    @Test
    fun `published policies do not follow later config changes`() {
        val config = ServerPolicyConfig()
        val policy = ServerPolicy.create(config)
        config.collision.allowIgnoring = false
        policy shouldBe ServerPolicy.ALLOW_ALL
    }
}
