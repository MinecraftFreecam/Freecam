package net.xolt.freecam.network

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PolicyBroadcasterTest {
    private val restricted = ServerPolicy(false, ServerPolicy.CollisionPolicy(false), false, false)

    @Test
    fun `waits for channel registration then sends once and broadcasts changes`() {
        val broadcaster = PolicyBroadcaster<Any>()
        val player = Any()
        val received = mutableListOf<ServerPolicy>()
        var supported = false
        val send = java.util.function.BiPredicate<Any, ServerPolicy> { _, policy ->
            if (supported) received.add(policy)
            supported
        }
        broadcaster.tick(listOf(player), restricted, send)
        received shouldBe emptyList()
        supported = true
        broadcaster.tick(listOf(player), restricted, send)
        broadcaster.tick(listOf(player), restricted, send)
        received shouldBe listOf(restricted)
        broadcaster.tick(listOf(player), ServerPolicy.ALLOW_ALL, send)
        received shouldBe listOf(restricted, ServerPolicy.ALLOW_ALL)
    }

    @Test
    fun `new connections and reconnects each receive the current policy`() {
        val broadcaster = PolicyBroadcaster<Any>()
        val first = Any()
        val second = Any()
        val received = mutableListOf<Any>()
        val send = java.util.function.BiPredicate<Any, ServerPolicy> { player, _ -> received.add(player); true }
        broadcaster.tick(listOf(first), restricted, send)
        broadcaster.tick(listOf(first, second), restricted, send)
        broadcaster.tick(emptyList(), restricted, send)
        broadcaster.tick(listOf(first), restricted, send)
        received shouldBe listOf(first, second, first)
    }

    @Test
    fun `unsupported clients do not prevent delivery to other clients`() {
        val broadcaster = PolicyBroadcaster<Any>()
        val vanilla = Any()
        val modded = Any()
        val received = mutableListOf<Any>()
        repeat(3) {
            broadcaster.tick(listOf(vanilla, modded), restricted) { player, _ ->
                if (player === modded) { received.add(player); true } else false
            }
        }
        received shouldBe listOf(modded)
    }
}
