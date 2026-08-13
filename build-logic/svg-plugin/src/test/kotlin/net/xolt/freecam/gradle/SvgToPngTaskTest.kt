package net.xolt.freecam.gradle

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.apache.batik.transcoder.image.PNGTranscoder
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import java.awt.Color
import java.awt.Rectangle
import kotlin.test.BeforeTest
import kotlin.test.Test

class SvgToPngTaskTest {

    private lateinit var project: Project

    @BeforeTest
    fun setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `task transcoder maps properties to Batik TranscodingHints`() {
        val tempStylesheet = project.layout.buildDirectory.file("style.css").get().asFile
        tempStylesheet.parentFile.mkdirs()
        tempStylesheet.writeText("/* dummy */")

        val task = project.tasks.register<SvgToPngTask>("testTask") {
            width.set(128f)
            height.set(256f)
            backgroundColor.set(Color.RED)
            areaOfInterest.set(Rectangle(0, 0, 10, 10))
            userStylesheet.set(tempStylesheet)
        }

        val hints = task.get().transcoder().transcodingHints

        withClue("task properties affect transcoding hints") {
            hints[PNGTranscoder.KEY_WIDTH] shouldBe 128f
            hints[PNGTranscoder.KEY_HEIGHT] shouldBe 256f
            hints[PNGTranscoder.KEY_BACKGROUND_COLOR] shouldBe Color.RED
            hints[PNGTranscoder.KEY_AOI] shouldBe Rectangle(0, 0, 10, 10)
            hints[PNGTranscoder.KEY_USER_STYLESHEET_URI] shouldBe tempStylesheet.toURI().toASCIIString()
        }

        withClue("Default hints are applied") {
            hints[PNGTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES] shouldBe true
        }
    }
}
