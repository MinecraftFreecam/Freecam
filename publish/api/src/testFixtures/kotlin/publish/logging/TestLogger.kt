package net.xolt.freecam.publish.logging

class TestLogger(
    override val scopes: List<String> = emptyList(),
    override var threshold: LogLevel = LogLevel.NONE,
    override var renderer: LogRenderer = DefaultLogRenderer,
) : Logger() {

    val logs: List<Pair<LogLevel, String>>
        get() = store.map { (ctx, rendered) ->
            ctx.level to rendered
        }

    val messages: List<String>
        get() = store.map { it.second }

    val logsWithContext: List<Pair<LogEvent, String>>
        get() = store.toList()

    private val store = mutableListOf<Pair<LogEvent, String>>()

    private val storeOutput: LogOutput = { rendered ->
        store += this to rendered
    }

    override var output: LogOutput
        get() = storeOutput
        set(value) = error("Attempted to mutate TestLogger.output")
}