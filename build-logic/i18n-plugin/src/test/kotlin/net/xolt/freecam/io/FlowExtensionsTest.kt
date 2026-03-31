package net.xolt.freecam.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class FlowExtensionsTest {

    @Test
    fun `concurrentMap transforms each element`() = runTest {
        val result = flowOf(1, 2, 3, 4)
                .concurrentMap { it * 2 }
                .toList()

        withClue("each element should be doubled") {
            result shouldContainExactlyInAnyOrder listOf(2, 4, 6, 8)
        }
    }

    @Test
    fun `concurrentMap respects concurrencyLevel`() = runTest {
        val duration = testScheduler.timeSource.measureTime {
            flowOf(1, 2, 3, 4, 5, 6, 7, 8)
                .concurrentMap(concurrencyLevel = 2) { delay(1.seconds); it }
                .toList()
        }

        withClue("4 batches of concurrent 1 second delays → 4 seconds") {
            duration shouldBe 4.seconds
        }
    }

    @Test
    fun `concurrentMap propagates exceptions`() = runTest {
        val ex = shouldThrow<RuntimeException> {
            flowOf(1, 2, 3).concurrentMap {
                if (it == 2) throw RuntimeException("fail") else it
            }.toList()
        }
        ex.message shouldBe "fail"
    }

    @Test
    fun `onEachConcurrent respects concurrencyLevel`() = runTest {
        val duration = testScheduler.timeSource.measureTime {
            flowOf(1, 2, 3, 4, 5, 6, 7, 8)
                .onEachConcurrent(concurrencyLevel = 2) { delay(1.seconds) }
                .toList()
        }

        withClue("4 batches of concurrent 1 second delays → 4 seconds") {
            duration shouldBe 4.seconds
        }
    }

    @Test
    fun `onEachConcurrent invokes the action once for each element`() = runTest {
        val fixture = listOf(1, 2, 3, 4)

        val seen = ConcurrentLinkedQueue<Int>()
        val result = fixture.asFlow()
            .onEachConcurrent { seen += it }
            .toList()

        seen shouldContainExactlyInAnyOrder fixture
        result shouldContainExactlyInAnyOrder fixture
    }

    @Test
    fun `onEachConcurrent propagates exceptions`() = runTest {
        val ex = shouldThrow<RuntimeException> {
            flowOf(1, 2, 3).onEachConcurrent {
                if (it == 2) throw RuntimeException("fail")
            }.collect()
        }
        ex.message shouldBe "fail"
    }
}
