import definition.parseComponentDefinition
import definition.parseFile
import definition.parseRobotProfile
import generation.generateCPPRobotHandler
import generation.prepareCPPDirectory

fun main(argsArray: Array<String>) {
    val args = argsArray.toMutableList()

    if (args.contains("-h")) {
        printUsage()
        return
    }

    val arduinoOutput = args.consumeOption("-ao")
    val pythonOutput = args.consumeOption("-po")
    val robotProfilePath = args.consumeOption("-r")
    val componentPaths = (args.consumeVarargOption("-c") ?: listOf())
            .map { "$it/config.txt" }

    val components = componentPaths.flatMap { parseFile(it, ::parseComponentDefinition) }
    val robotProfiles = robotProfilePath?.let { parseFile(it, ::parseRobotProfile) } ?: listOf()

    if (arduinoOutput != null) {
        prepareCPPDirectory(arduinoOutput, components)
        robotProfiles.forEach { generateCPPRobotHandler(arduinoOutput, it) }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

private fun printUsage() {
    println("""
        Usage: rcs-gen <options>
        
        Options:
            -ao : specify output path for generated Arduino code
            -po : specify output path for generated Python code
            -r  : specify robot profile file path
            -c  : specify component definition paths
            
        Example:
            rcs-gen -ao src/gen/arduino -po src/gen/python
                    -r etc/primary.txt -c meta/components/* etc/custom-components/*
    """.trimIndent())
}

////////////////////////////////////////////////////////////////////////////////

private fun MutableList<String>.consumeVarargOption(option: String): List<String>? {
    val index = indexOf(option)

    if (index != -1) {
        removeAt(index)

        val results = mutableListOf<String>()

        while (index < size && !this[index].startsWith("-")) {
            results.add(removeAt(index))
        }

        return results
    }
    return null
}

private fun MutableList<String>.consumeOption(option: String): String? {
    val index = indexOf(option)

    return if (index != -1 && index < size - 1) {
        removeAt(index)
        removeAt(index)
    }
    else null
}
