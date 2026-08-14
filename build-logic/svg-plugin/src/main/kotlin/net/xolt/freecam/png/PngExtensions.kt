package net.xolt.freecam.png

import com.googlecode.pngtastic.core.PngImage
import com.googlecode.pngtastic.core.PngOptimizer
import java.io.ByteArrayOutputStream
import java.io.File

private val optimizer by lazy {
    PngOptimizer()
}

/**
 * Optimizes a PNG file in-place using [Pngtastic][PngOptimizer].
 *
 * This reads the image into memory, optimizes its palette and deflate compression,
 * and overwrites the original file only if the resulting file size is smaller.
 *
 * @return true if the file was optimized and updated, false if the original was kept.
 */
fun File.optimizePng(removeGamma: Boolean = false, compressionLevel: Int? = null): Boolean {
    require(exists() && isFile) {
        "File must exist to be optimized: $absolutePath"
    }

    val originalSize = length()
    val image = inputStream().buffered().use { input ->
        optimizer.optimize(PngImage(input), removeGamma, compressionLevel)
    }
    val optimized = ByteArrayOutputStream().also { output ->
        image.writeDataOutputStream(output)
    }.toByteArray()

    // Only overwrite if the optimized size is actually smaller
    if (optimized.size < originalSize) {
        writeBytes(optimized)
        return true
    }

    return false
}
