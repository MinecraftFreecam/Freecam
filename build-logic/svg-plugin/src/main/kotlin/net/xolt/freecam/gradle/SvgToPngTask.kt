package net.xolt.freecam.gradle

import net.xolt.freecam.png.optimizePng
import net.xolt.freecam.transcoder.HighQualityPngTranscoder
import net.xolt.freecam.transcoder.transcodeTo
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.awt.Color
import java.awt.Rectangle

@CacheableTask
abstract class SvgToPngTask : DefaultTask() {

    /**
     * The SVG to rasterize.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: RegularFileProperty

    /**
     * Optional user stylesheet applied to the SVG.
     */
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val userStylesheet: RegularFileProperty

    /**
     * The PNG file to write.
     */
    @get:OutputFile
    abstract val destination: RegularFileProperty

    /**
     * Width of the PNG in pixels.
     */
    @get:Input
    @get:Optional
    abstract val width: Property<Float>

    /**
     * Height of the PNG in pixels.
     */
    @get:Input
    @get:Optional
    abstract val height: Property<Float>

    /**
     * Override the transparent background.
     */
    @get:Input
    @get:Optional
    abstract val backgroundColor: Property<Color>

    /**
     * Draw a rectangular area of interest.
     */
    @get:Input
    @get:Optional
    abstract val areaOfInterest: Property<Rectangle>

    /**
     * Optional PNG compression level.
     */
    @get:Input
    @get:Optional
    abstract val compressionLevel: Property<Int>

    internal fun transcoder(): ImageTranscoder = HighQualityPngTranscoder().also { transcoder ->
        width.orNull?.let {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, it)
        }
        height.orNull?.let {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, it)
        }
        areaOfInterest.orNull?.let {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_AOI, it)
        }
        backgroundColor.orNull?.let {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, it)
        }
        userStylesheet.asFile.orNull?.let {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_USER_STYLESHEET_URI, it.toURI().toASCIIString())
        }
        transcoder.addTranscodingHint(PNGTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, true)
    }

    @TaskAction
    fun execute() {
        val source = source.asFile.get()
        val destination = destination.asFile.get()
        source.transcodeTo(
            destination = destination,
            transcoder = transcoder(),
        )
        destination.optimizePng(
            compressionLevel = compressionLevel.orNull,
        )
    }
}
