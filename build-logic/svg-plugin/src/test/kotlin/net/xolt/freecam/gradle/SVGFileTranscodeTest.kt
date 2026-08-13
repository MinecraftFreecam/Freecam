package net.xolt.freecam.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import net.xolt.freecam.transcoder.HighQualityPngTranscoder
import net.xolt.freecam.transcoder.transcodeTo
import org.apache.batik.transcoder.TranscoderException
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.BeforeTest
import kotlin.test.Test

class SVGFileTranscodeTest {

    lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory().toFile().apply {
            deleteOnExit()
        }
    }

    @Test
    fun `rasterizeTo can transcode a valid SVG to a PNG file`() {
        val sourceSvg = tempDir.resolve("test.svg").apply {
            writeText("""
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
                    <rect width="16" height="16" fill="#FF0000"/>
                </svg>
            """.trimIndent())
        }
        val destPng = tempDir.resolve("output.png")
        val transcoder = HighQualityPngTranscoder()

        sourceSvg.transcodeTo(destPng, transcoder)

        destPng.shouldExist()
        destPng.length().shouldBeGreaterThan(0L)

        // Verify PNG magic bytes (89 50 4E 47 0D 0A 1A 0A) to ensure it is actually an image
        val expectedMagicBytes = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
        val actualMagicBytes = destPng.readBytes().take(8).toByteArray()

        actualMagicBytes shouldBe expectedMagicBytes
    }

    @Test
    fun `rasterizeTo throws TranscoderException for invalid SVG markup`() {
        val invalidSvg = tempDir.resolve("invalid.svg").apply {
            writeText("<svg> missing closing tags and invalid XML")
        }
        val destPng = tempDir.resolve("output_invalid.png")
        val transcoder = HighQualityPngTranscoder()

        shouldThrow<TranscoderException> {
            invalidSvg.transcodeTo(destPng, transcoder)
        }
    }

    @Test
    fun `rasterizeTo creates missing parent directories for the destination file`() {
        val sourceSvg = tempDir.resolve("test.svg").apply {
            writeText("""<svg xmlns="http://www.w3.org/2000/svg" width="1" height="1"></svg>""")
        }
        // Deeply nested, non-existent path
        val destPng = tempDir.resolve("deeply/nested/output/dir/output.png")
        val transcoder = HighQualityPngTranscoder()

        sourceSvg.transcodeTo(destPng, transcoder)

        destPng.shouldExist()
    }
}
