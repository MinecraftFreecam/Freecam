package net.xolt.freecam.test

import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

fun createTestDir(): Path = createTempDirectory().apply {
    toFile().deleteOnExit()
}

fun createTestFile(): Path = createTempFile().apply {
    toFile().deleteOnExit()
}

fun createTestFile(name: String): Path =
    createTestDir().resolve(name).apply {
        createFile()
    }
