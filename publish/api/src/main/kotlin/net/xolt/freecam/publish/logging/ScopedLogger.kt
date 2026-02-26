package net.xolt.freecam.publish.logging

internal class ScopedLogger(
    val parent: Logger,
    scope: String,
    @Volatile
    private var localLevel: LogLevel? = null,
    @Volatile
    private var localRenderer: LogRenderer? = null,
    @Volatile
    private var localOutput: LogOutput? = null,
) : Logger() {

    override val scopes: List<String> = parent.scopes + scope

    override var threshold
        get() = localLevel?.coerceAtMost(parent.threshold) ?: parent.threshold
        set(value) { localLevel = value }

    override var renderer
        get() = localRenderer ?: parent.renderer
        set(value) { localRenderer = value }

    override var output
        get() = localOutput ?: parent.output
        set(value) { localOutput = value }
}