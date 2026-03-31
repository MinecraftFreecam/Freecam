package net.xolt.freecam.i18n

internal inline fun <reified T> withErrorContext(context: () -> String, block: () -> T): T =
    try {
        block()
    } catch (e: IllegalStateException) {
        throw IllegalStateException(composeMessages(context(), e.message), e)
    }

private fun composeMessages(context: String, message: String?) =
    sequenceOf(context, message.takeUnless { it.isNullOrBlank() })
        .filterNotNull()
        .joinToString(":\n")
