import cpp.ArduinoCodeGenerator
import cpp.ComponentSourceDefinition
import cpp.loadComponentSourceDefinition
import kt.KotlinCodeGenerator
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val arguments = parseArguments(args)
    val robot = readJSONFile(arguments.robotConfigPath, jsonDecodeRobotDefinition)
    val components = arguments.componentConfigPaths.map(::loadComponentSourceDefinition)

    if (arguments.arduinoOutputPath != null) {
        val gen = ArduinoCodeGenerator(robot, components)
        gen.buildCompileFolder(arguments.arduinoOutputPath)
    }

    if (arguments.kotlinOutputPath != null) {
        val gen = KotlinCodeGenerator(robot, components.map { it.component })
        gen.buildControllerFiles(arguments.kotlinOutputPath)
    }
}

class GenerationParameters(
        val robotConfigPath: String,
        val componentConfigPaths: Set<String>,
        val arduinoOutputPath: String?,
        val pythonOutputPath: String?,
        val kotlinOutputPath: String?
)

enum class GenerationTarget {
    Arduino,
    Kotlin,
    Python
}

//////////////////////////////////////////////////////////////////////////////////////////

private fun parseArguments(args: Array<String>): GenerationParameters {
    val arguments = mutableListOf(*args)

    fun argValue(flag: String): String? {
        val index = arguments.indexOf(flag)

        if (index != -1) {
            arguments.removeAt(index)
            return arguments.removeAt(index)
        }

        return null
    }

    val genPython = argValue("-gp")
    val genKotlin = argValue("-gk")
    val genArduino = argValue("-ga")

    if (genPython == null && genKotlin == null && genArduino == null) cliError("Expected at least one target")
    if (arguments.isEmpty()) cliError("Expected output path")
    if (arguments.size < 2) cliError("Expected robot configuration path")

    return GenerationParameters(
            arguments[0],
            arguments.drop(1).toSet(),
            genArduino,
            genPython,
            genKotlin
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
