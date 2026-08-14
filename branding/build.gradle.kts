import org.gradle.internal.DefaultTaskExecutionRequest

plugins {
    id("freecam.svg")
}

val sourceDir = layout.projectDirectory.dir("src/main")

svg.exportPngMatrix("icon", 24, 32, 48, 64, 96, 128, 192, 256, 512, 1024) {
    source = sourceDir.file("icon.svg")
}

val installProjectIconTask = tasks.register<Copy>("installProjectIcon") {
    description = "Install the IntelliJ IDEA project icon"
    from(sourceDir.file("icon.svg"))
    into(rootDir.resolve(".idea"))
}

// Hack to make IntelliJ IDEA run installProjectIconTask on sync
if (providers.systemProperty("idea.sync.active").map { it.toBoolean() }.getOrElse(false)) {
    val taskPath = listOf(project.path, installProjectIconTask.name).joinToString(":")
    gradle.startParameter.taskRequests += DefaultTaskExecutionRequest(listOf(taskPath))
}
