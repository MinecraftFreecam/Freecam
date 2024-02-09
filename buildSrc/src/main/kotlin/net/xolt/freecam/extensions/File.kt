package net.xolt.freecam.extensions

import java.io.File

fun File.childDirectories(): Sequence<File> = listFiles { file -> file.isDirectory }?.asSequence() ?: emptySequence()