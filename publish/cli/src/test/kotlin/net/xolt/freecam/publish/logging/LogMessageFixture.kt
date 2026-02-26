package net.xolt.freecam.publish.logging

fun testLogMessage(
    level: LogLevel = LogLevel.INFO,
    scopes: List<String> = emptyList(),
    message: String = "",
) = LogEvent(
    level = level,
    scopes = scopes,
    message = message,
)
