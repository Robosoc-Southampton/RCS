import kotlin.math.max

private val disabled: MutableSet<String> = HashSet()

/** Provides slightly more fancy logging capabilities */
object Logging {
    private var padding = 28

    /** Enables a previously disabled log type */
    fun enable(type: String) {
        padding = max(padding, type.length)
        disabled.remove(type)
    }

    /** Disables a log type (causing messages not to be shown) */
    fun disable(type: String) {
        padding = max(padding, type.length)
        disabled.add(type)
    }

    /** Logs a message with colour formatting.
     *
     *  Colours include:
     *
     *  * red
     *  * green
     *  * yellow
     *  * blue
     *  * magenta
     *  * cyan
     *  * white
     *
     *  To set a colour, use %c (or %C for bold equivalent) where c is the first
     *  letter of the colour.
     *  Use %- (or %~ for bold equivalent) to reset text colour. */
    fun log(type: String, message: String) {
        padding = max(padding, type.length)
        if (!disabled.contains(type)) {
            val typeFormatted = ("${type.toUpperCase()}:").padEnd(padding + 2)
            println("\u001B[37m $typeFormatted\u001B[0m ${fmt(message)}")
        }
    }

    /** Logs a formatted message with colour formatting.
     *
     *  Colours include:
     *
     *  * red
     *  * green
     *  * yellow
     *  * blue
     *  * magenta
     *  * cyan
     *  * white
     *
     *  To format a coloured item, use %c (or %C for bold equivalent) where c is
     *  the first letter of the colour. */
    fun logf(type: String, message: String, vararg values: Any?) {
        var i = 0

        log(type, message.replace(Regex("('?)%(.)('?)")) { result ->
            val (_, open, col, close) = result.groupValues
            values[i++].let { "%$col$open$it$close%-" }
        })
    }

    /** Logs a warning with colour formatting.
     *  Non coloured text is replaced with bold yellow.
     *
     *  Colours include:
     *
     *  * red
     *  * green
     *  * yellow
     *  * blue
     *  * magenta
     *  * cyan
     *  * white
     *
     *  To set a colour, use %c (or %C for bold equivalent) where c is the first
     *  letter of the colour.
     *  Use %- (or %~ for bold equivalent) to reset text colour. */
    fun warn(type: String, message: String) {
        log(
                type, "%Y" + message
                .replace("%-", "%Y")
                .replace("%~", "%Y")
        )
    }
}


private fun fmt(text: String): String {
    return text
            .replace("%r", "\u001B[31m")
            .replace("%g", "\u001B[32m")
            .replace("%y", "\u001B[33m")
            .replace("%b", "\u001B[34m")
            .replace("%m", "\u001B[35m")
            .replace("%c", "\u001B[36m")
            .replace("%w", "\u001B[37m")
            .replace("%-", "\u001B[0m")
            .replace("%R", "\u001B[31;1m")
            .replace("%G", "\u001B[32;1m")
            .replace("%Y", "\u001B[33;1m")
            .replace("%B", "\u001B[34;1m")
            .replace("%M", "\u001B[35;1m")
            .replace("%C", "\u001B[36;1m")
            .replace("%W", "\u001B[37;1m")
            .replace("%~", "\u001B[0;1m")
}
