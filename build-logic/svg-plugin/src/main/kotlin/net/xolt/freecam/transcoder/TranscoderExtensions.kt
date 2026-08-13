package net.xolt.freecam.transcoder

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import java.io.File

/**
 * Transcode using Batik's streaming API.
 */
internal fun File.transcodeTo(destination: File, transcoder: ImageTranscoder) {
    val baseUri = toURI().toASCIIString()
    destination.parentFile?.mkdirs()
    inputStream().buffered().use { input ->
        destination.outputStream().buffered().use { output ->
            transcoder.transcode(
                TranscoderInput(input).apply {
                    uri = baseUri
                },
                TranscoderOutput(output),
            )
        }
    }
}
