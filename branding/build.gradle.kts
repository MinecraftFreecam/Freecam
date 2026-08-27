import org.gradle.internal.DefaultTaskExecutionRequest
import java.awt.Color

plugins {
    id("freecam.svg")
}

val sourceDir = layout.projectDirectory.dir("src/main")
val iconBackground = Color(0x002a36)
val iconForeground = Color(0x00607c)

val Color.xmlHexRgb
    get() = (rgb and 0xffffff).toHexString(HexFormat {
        number {
            prefix = "#"
            minLength = 6
            removeLeadingZeros = true
        }
    })

val styleSheetTask = tasks.register("iconStyleSheet") {
    description = "Generate the icon's CSS stylesheet"
    inputs.properties("foreground" to iconForeground)
    outputs.file(layout.buildDirectory.file("resources/icon.css"))
    doLast {
        outputs.files.singleFile.apply {
            writeText("svg { color: ${iconForeground.xmlHexRgb}; }")
        }
    }
}

svg.exportPngMatrix("icon", 24, 32, 48, 64, 96, 100, 128, 192, 256, 512, 1024) {
    source = sourceDir.file("icon.svg")
    userStylesheet = styleSheetTask.map { it.outputs.files.singleFile }
    backgroundColor = iconBackground
}

val installProjectIconTask = tasks.register<Copy>("installProjectIcon") {
    description = "Install the IntelliJ IDEA project icon"
    from(sourceDir.file("icon.svg"))
    into(rootDir.resolve(".idea"))

    inputs.properties(
        "background" to iconBackground,
        "foreground" to iconForeground,
    )

    filter { line ->
        when {
            line.startsWith("<svg ") ->
                """
                |$line
                |  <rect width="100%" height="100%" fill="${iconBackground.xmlHexRgb}"/>
                """.trimMargin()
            line.contains("\"currentColor\"") ->
                line.replace("\"currentColor\"", "\"${iconForeground.xmlHexRgb}\"")
            else -> line
        }
    }
}

// Hack to make IntelliJ IDEA run installProjectIconTask on sync
if (providers.systemProperty("idea.sync.active").map { it.toBoolean() }.getOrElse(false)) {
    val taskPath = listOf(project.path, installProjectIconTask.name).joinToString(":")
    gradle.startParameter.taskRequests += DefaultTaskExecutionRequest(listOf(taskPath))
}
