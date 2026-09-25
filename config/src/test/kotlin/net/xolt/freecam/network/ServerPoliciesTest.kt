package net.xolt.freecam.network

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ServerPoliciesTest {
    private val serverPolicies = ServerPolicies()

    private fun policies() = listOf(
        serverPolicies.allowFreecam(),
        serverPolicies.allowIgnoringCollision(),
        serverPolicies.allowFullbright(),
        serverPolicies.allowCameraInteractions(),
    )

    @Test
    fun `servers without a policy allow all features`() {
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `raw plugin message applies independent restrictions`() {
        serverPolicies.applyBytes("""{"collision":{"allowIgnoring":false},"allowCameraInteractions":false}""".toByteArray()) shouldBe true
        policies() shouldBe listOf(true, false, true, false)
    }

    @Test
    fun `updates replace the policy and default omitted fields to allowed`() {
        serverPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false},"allowFullbright":false,"allowCameraInteractions":false}""") shouldBe true
        policies() shouldBe listOf(false, false, false, false)
        serverPolicies.applyJson("""{"allowFullbright":false}""") shouldBe true
        policies() shouldBe listOf(true, true, false, true)
        serverPolicies.applyJson("{}") shouldBe true
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `invalid updates leave the previous policy intact`() {
        serverPolicies.applyJson("""{"allowFreecam":false}""") shouldBe true
        for (json in listOf("", "null", "[]", "true", "{", "{} trailing",
            "{allowFreecam:true}", "{'allowFreecam':true}", "{/*comment*/}",
            """{"allowFreecam":true,"allowCameraInteractions":"false"}""",
            """{"collision":{"allowIgnoring":null}}""", """{"collision":false}""",
            """{"allowFullbright":0}""")) {
            serverPolicies.applyJson(json) shouldBe false
            policies() shouldBe listOf(false, true, true, true)
        }
    }

    @Test
    fun `unknown fields are ignored for forward compatibility`() {
        serverPolicies.applyJson("""{"futurePolicy":{"value":false},"collision":{"allowIgnoringTransparent":false},"allowCameraInteractions":false}""") shouldBe true
        policies() shouldBe listOf(true, true, true, false)
    }

    @Test
    fun `disconnect reset restores permissions before the next server`() {
        serverPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false},"allowFullbright":false,"allowCameraInteractions":false}""") shouldBe true
        serverPolicies.reset()
        policies() shouldBe listOf(true, true, true, true)
    }
    @Test
    fun `AntiFreecam boolean only restricts ignoring collision`() {
        serverPolicies.applyAntiFreecamBytes(byteArrayOf(1)) shouldBe true
        policies() shouldBe listOf(true, false, true, true)
        serverPolicies.applyAntiFreecamBytes(byteArrayOf(0)) shouldBe true
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `protocols cannot lift each others restrictions`() {
        serverPolicies.applyAntiFreecam(true)
        serverPolicies.applyJson("{}") shouldBe true
        serverPolicies.allowIgnoringCollision() shouldBe false
        serverPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false}}""") shouldBe true
        serverPolicies.applyAntiFreecam(false)
        policies() shouldBe listOf(false, false, true, true)
    }

    @Test
    fun `malformed AntiFreecam packets preserve the previous restriction`() {
        serverPolicies.applyAntiFreecam(true)
        serverPolicies.applyAntiFreecamBytes(byteArrayOf()) shouldBe false
        serverPolicies.applyAntiFreecamBytes(byteArrayOf(0, 0)) shouldBe false
        serverPolicies.allowIgnoringCollision() shouldBe false
        serverPolicies.reset()
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `host policy replaces received restrictions while hosting`() {
        serverPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false}}""") shouldBe true
        serverPolicies.applyAntiFreecam(true)
        serverPolicies.setHostPolicy(ServerPolicy.ALLOW_ALL)
        policies() shouldBe listOf(true, true, true, true)

        serverPolicies.setHostPolicy(ServerPolicy(true, ServerPolicy.CollisionPolicy(true), false, true))
        policies() shouldBe listOf(true, true, false, true)

        serverPolicies.setHostPolicy(null)
        policies() shouldBe listOf(false, false, true, true)
    }

    @Test
    fun `reset clears the host policy`() {
        serverPolicies.setHostPolicy(ServerPolicy(false, ServerPolicy.CollisionPolicy(false), false, false))
        serverPolicies.reset()
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `policies serialize to the documented wire format`() {
        ServerPolicy(true, ServerPolicy.CollisionPolicy(false), true, false).toJson() shouldBe
            """{"allowFreecam":true,"collision":{"allowIgnoring":false},"allowFullbright":true,"allowCameraInteractions":false}"""
    }

    @Test
    fun `server snapshots round trip every policy combination`() {
        for (bits in 0..15) {
            val policy = ServerPolicy(bits and 1 != 0, ServerPolicy.CollisionPolicy(bits and 2 != 0), bits and 4 != 0, bits and 8 != 0)
            serverPolicies.applyJson(policy.toJson()) shouldBe true
            policies() shouldBe listOf(policy.allowFreecam(), policy.collision().allowIgnoring(), policy.allowFullbright(), policy.allowCameraInteractions())
        }
    }
}
