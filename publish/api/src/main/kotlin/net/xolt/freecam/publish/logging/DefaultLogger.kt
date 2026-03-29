package net.xolt.freecam.publish.logging

internal class DefaultLogger(
    override val scopes: List<String> = emptyList(),
    @Volatile
    override var threshold: LogLevel = LogLevel.INFO,
    @Volatile
    override var renderer: LogRenderer = DefaultLogRenderer,
    @Volatile
    override var output: LogOutput = PrintLogOutput,
) : Logger()