package rcs.util

import kotlin.system.exitProcess

class ArgumentParser internal constructor(private val name: String, private val switches: List<Switch>) {
    fun parse(args: Array<String>): ProgramArguments {
        val arguments = args.toMutableList()
        val results = mutableMapOf<String, List<String>>()
        var errored = false

        if (arguments.size == 1 && arguments[0] in listOf("-h", "--help", "help")) {
            printUsage()
            exitProcess(0)
        }

        switches.forEach { switch ->
            val index = arguments.indexOf(switch.shorthand)

            if (index == -1) {
                if (switch.count == null)
                    results[switch.name] = listOf()
                else if (!switch.optional) {
                    System.err.println("Expected '${switch.name}' argument ('${switch.shorthand}')")
                    errored = true
                }
            }
            else if (switch.count != null && arguments.size - index - 1 < switch.count) {
                System.err.println("Expected '${switch.count}' parameters to argument '${switch.name}' ('${switch.shorthand}')")
                errored = true
            }
            else {
                val p = arguments.drop(index).drop(1).take(switch.count ?: arguments.size - index - 1)
                results[switch.name] = p
                repeat(p.size) { arguments.removeAt(index) }
            }
        }

        if (errored) {
            printUsage()
            exitProcess(1)
        }

        return ProgramArguments(results)
    }

    fun printUsage() {
        System.err.flush()
        System.out.flush()
        println("Usage: $name")
        switches.forEach { switch ->
            print("\t")
            if (switch.optional) print("[")
            print(switch.shorthand)
            if (switch.count == 1) print(" <${switch.name}>")
            if (switch.count == null) print(" <${switch.name}rcs.getS...>")
            else if (switch.count > 1) print((1 .. switch.count).joinToString("") { " <${switch.name} $it>" })
            if (switch.optional) print("]")
            println(" - ${switch.description}")
        }
    }

    companion object {
        fun create(name: String, fn: ArgumentParserBuilder.() -> Unit)
                = ArgumentParserBuilder().apply(fn).build(name)
    }
}

class ProgramArguments(private val results: MutableMap<String, List<String>>) {
    fun value(name: String) = results[name]!![0]
    fun optionalValue(name: String) = results[name]?.get(0)
    fun values(name: String) = results[name]!!
    fun optionalValues(name: String) = results[name]
    fun flag(name: String) = results[name] != null
}

class ArgumentParserBuilder internal constructor() {
    fun switch(name: String, shorthand: String = "-$name", count: Int? = 0, optional: Boolean = false, description: String = name) {
        switches.add(Switch(name, shorthand, count, optional, description))
    }

    fun flag(name: String, shorthand: String = "-$name", description: String) {
        switch(name, shorthand, count = 0, optional = true, description = description)
    }

    internal fun build(name: String): ArgumentParser {
        return ArgumentParser(name, switches)
    }

    private val switches = mutableListOf<Switch>()
}

internal data class Switch(val name: String, val shorthand: String, val count: Int?, val optional: Boolean, val description: String)

fun main(args: Array<String>) {
    val parser = ArgumentParser.create("rcs-gen") {
        switch("output path", "-o", count = 1,
                description = "Path where the folder should be generated.")
        switch("static routine", "-rcs.getS", count = 1, optional = true,
                description = "File containing a static routine.")
    }

    val result = parser.parse(args)

    print(result.value("output path"))
}
