package net.xolt.freecam.transcoder

import org.apache.batik.gvt.renderer.ImageRenderer
import org.apache.batik.transcoder.image.PNGTranscoder
import java.awt.RenderingHints

internal class HighQualityPngTranscoder : PNGTranscoder() {

    private val customRenderingHints = mapOf(
        RenderingHints.KEY_ALPHA_INTERPOLATION to RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
        RenderingHints.KEY_INTERPOLATION to RenderingHints.VALUE_INTERPOLATION_BICUBIC,
        RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
        RenderingHints.KEY_COLOR_RENDERING to RenderingHints.VALUE_COLOR_RENDER_QUALITY,
        RenderingHints.KEY_DITHERING to RenderingHints.VALUE_DITHER_DISABLE,
        RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY,
        RenderingHints.KEY_STROKE_CONTROL to RenderingHints.VALUE_STROKE_PURE,
        RenderingHints.KEY_FRACTIONALMETRICS to RenderingHints.VALUE_FRACTIONALMETRICS_ON,
        RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
    )

    override fun createRenderer(): ImageRenderer = super.createRenderer().apply {
        @Suppress("UNCHECKED_CAST")
        val hints = (renderingHints + customRenderingHints) as Map<RenderingHints.Key, Any>
        renderingHints = RenderingHints(hints)
    }
}
