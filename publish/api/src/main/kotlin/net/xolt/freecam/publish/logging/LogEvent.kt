package net.xolt.freecam.publish.logging

data class LogEvent(
    val level: LogLevel,
    val scopes: List<String>,
    val message: String,
)
