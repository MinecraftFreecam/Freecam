package net.xolt.freecam.network

import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ServerPoliciesTest {
    @BeforeTest
    fun setup() = ServerPolicies.reset()

    @AfterTest
    fun cleanup() = ServerPolicies.reset()

    private fun policies() = listOf(
        ServerPolicies.allowFreecam(),
        ServerPolicies.allowIgnoringCollision(),
        ServerPolicies.allowFullbright(),
        ServerPolicies.allowInteract(),
    )

    @Test
    fun `servers without a policy allow all features`() {
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `raw plugin message applies independent restrictions`() {
        ServerPolicies.applyBytes("""{"collision":{"allowIgnoring":false},"allowInteract":false}""".toByteArray()) shouldBe true
        policies() shouldBe listOf(true, false, true, false)
    }

    @Test
    fun `updates replace the policy and default omitted fields to allowed`() {
        ServerPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false},"allowFullbright":false,"allowInteract":false}""") shouldBe true
        policies() shouldBe listOf(false, false, false, false)
        ServerPolicies.applyJson("""{"allowFullbright":false}""") shouldBe true
        policies() shouldBe listOf(true, true, false, true)
        ServerPolicies.applyJson("{}") shouldBe true
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `invalid updates leave the previous policy intact`() {
        ServerPolicies.applyJson("""{"allowFreecam":false}""") shouldBe true
        for (json in listOf("", "null", "[]", "true", "{", "{} trailing",
            "{allowFreecam:true}", "{'allowFreecam':true}", "{/*comment*/}",
            """{"allowFreecam":true,"allowInteract":"false"}""",
            """{"collision":{"allowIgnoring":null}}""", """{"collision":false}""",
            """{"allowFullbright":0}""")) {
            ServerPolicies.applyJson(json) shouldBe false
            policies() shouldBe listOf(false, true, true, true)
        }
    }

    @Test
    fun `unknown fields are ignored for forward compatibility`() {
        ServerPolicies.applyJson("""{"futurePolicy":{"value":false},"collision":{"allowIgnoringTransparent":false},"allowInteract":false}""") shouldBe true
        policies() shouldBe listOf(true, true, true, false)
    }

    @Test
    fun `disconnect reset restores permissions before the next server`() {
        ServerPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false},"allowFullbright":false,"allowInteract":false}""") shouldBe true
        ServerPolicies.reset()
        policies() shouldBe listOf(true, true, true, true)
    }
    @Test
    fun `AntiFreecam boolean only restricts ignoring collision`() {
        ServerPolicies.applyAntiFreecamBytes(byteArrayOf(1)) shouldBe true
        policies() shouldBe listOf(true, false, true, true)
        ServerPolicies.applyAntiFreecamBytes(byteArrayOf(0)) shouldBe true
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `protocols cannot lift each others restrictions`() {
        ServerPolicies.applyAntiFreecam(true)
        ServerPolicies.applyJson("{}") shouldBe true
        ServerPolicies.allowIgnoringCollision() shouldBe false
        ServerPolicies.applyJson("""{"allowFreecam":false,"collision":{"allowIgnoring":false}}""") shouldBe true
        ServerPolicies.applyAntiFreecam(false)
        policies() shouldBe listOf(false, false, true, true)
    }

    @Test
    fun `malformed AntiFreecam packets preserve the previous restriction`() {
        ServerPolicies.applyAntiFreecam(true)
        ServerPolicies.applyAntiFreecamBytes(byteArrayOf()) shouldBe false
        ServerPolicies.applyAntiFreecamBytes(byteArrayOf(0, 0)) shouldBe false
        ServerPolicies.allowIgnoringCollision() shouldBe false
        ServerPolicies.reset()
        policies() shouldBe listOf(true, true, true, true)
    }

    @Test
    fun `server snapshots round trip every policy combination`() {
        for (bits in 0..15) {
            val policy = ServerPolicy(bits and 1 != 0, bits and 2 != 0, bits and 4 != 0, bits and 8 != 0)
            ServerPolicies.applyJson(policy.toJson()) shouldBe true
            policies() shouldBe listOf(policy.allowFreecam(), policy.allowIgnoringCollision(), policy.allowFullbright(), policy.allowInteract())
        }
    }
}
