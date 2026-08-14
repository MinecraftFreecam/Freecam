package net.xolt.freecam.png

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.createTempDirectory
import kotlin.test.BeforeTest
import kotlin.test.Test

class OptimizePngFileTest {

    lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory().toFile().apply {
            deleteOnExit()
        }
    }

    @Test
    fun `optimizePng reduces file size of an unoptimized PNG and returns true`() {
        val testFile = tempDir.resolve("test.png")
        testFile.createUnoptimizedPng()
        val originalSize = testFile.length()

        val result = testFile.optimizePng()

        result.shouldBeTrue()
        testFile.length() shouldBeLessThan originalSize

        withClue("PNG magic bytes remain valid") {
            val expectedMagicBytes = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
            val actualMagicBytes = testFile.readBytes().take(8).toByteArray()
            actualMagicBytes shouldBe expectedMagicBytes
        }
    }

    @Test
    fun `optimizePng does not modify file and returns false if optimization yields no improvement`() {
        val testFile = tempDir.resolve("tiny.png")
        testFile.createUnoptimizedPng()

        // Pre-optimize it so it's as small as possible
        testFile.optimizePng()
        val optimizedSize = testFile.length()

        // Running it again should yield no further size reduction
        val result = testFile.optimizePng()

        result.shouldBeFalse()
        testFile.length() shouldBe optimizedSize
    }

    @Test
    fun `optimizePng throws IllegalArgumentException if file does not exist`() {
        val nonExistentFile = tempDir.resolve("missing.png")

        shouldThrow<IllegalArgumentException> {
            nonExistentFile.optimizePng()
        }
    }

    /**
     * Java's ImageIO writes unoptimized PNGs, making a useful test fixture.
     */
    private fun File.createUnoptimizedPng() {
        val image = BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)
        image.createGraphics().apply {
            color = Color.BLUE
            fillRect(0, 0, 64, 64)
            color = Color(255, 0, 0, 128)
            fillOval(16, 16, 32, 32)
            dispose()
        }
        ImageIO.write(image, "png", this)
    }
}
