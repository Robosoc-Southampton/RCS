import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val arguments = parseArguments(args)

    val robot = readJSONFile(arguments.robotConfigPath, jsonDecodeRobotDefinition)
    val components = arguments.componentConfigPaths.map {
        val config = readJSONFile("$it/config.json", jsonDecodeComponentDefinition)
        val header = readFile("$it/header.h")
        val source = readFile("$it/source.cpp")

        ComponentSourceDefinition(config, header, source)
    }

    if (arguments.targets.contains(GenerationTarget.Arduino))
        ArduinoCodeGenerator(robot, components).buildCompileFolder(arguments.outputPath)
}

class GenerationParameters(
        val targets: Set<GenerationTarget>,
        val robotConfigPath: String,
        val componentConfigPaths: Set<String>,
        val outputPath: String
)

enum class GenerationTarget {
    Arduino,
    PythonBluetooth,
    PythonSimulator,
    PythonPrinter
}

//////////////////////////////////////////////////////////////////////////////////////////

private fun parseArguments(args: Array<String>): GenerationParameters {
    val arguments = mutableListOf(*args)
    val generateAll = arguments.remove("-ga")
    val generateArduino = arguments.remove("-gr") || generateAll
    val generatePythonBluetooth = arguments.remove("-gb") || generateAll
    val generatePythonSimulator = arguments.remove("-gs") || generateAll
    val generatePythonPrinter = arguments.remove("-gp") || generateAll
    val targets = setOf(
            GenerationTarget.Arduino.takeIf { generateArduino },
            GenerationTarget.PythonBluetooth.takeIf { generatePythonBluetooth },
            GenerationTarget.PythonSimulator.takeIf { generatePythonSimulator },
            GenerationTarget.PythonPrinter.takeIf { generatePythonPrinter }
    ).filterNotNull().toSet()

    if (targets.isEmpty()) cliError("Expected at least one target")
    if (arguments.isEmpty()) cliError("Expected output path")
    if (arguments.size < 2) cliError("Expected robot configuration path")

    return GenerationParameters(
            targets,
            arguments[1],
            arguments.drop(2).toSet(),
            arguments[0]
    )
}

private fun cliError(message: String) {
    printUsage()
    System.err.println(message)
    exitProcess(1)
}

private fun printUsage() {
    println("Usage rcs-gen [-ga|-gr|-gb|-gs|-gp] <robot configuration path> <output path> <component definition paths...>")
}

private fun readFile(file: String)
        = String(Files.readAllBytes(Paths.get(file)))

private fun <T> readJSONFile(file: String, decoder: JSONDecoder<T>)
        = decoder(jsonParse(readFile(file)))
