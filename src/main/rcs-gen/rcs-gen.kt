import cpp.ArduinoCodeGenerator
import cpp.ComponentSourceDefinition
import cpp.StaticRoutineReader
import cpp.loadComponentSourceDefinition
import kt.KotlinCodeGenerator
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CyclicBarrier
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val arguments = parseArguments(args)
    val robot = readJSONFile(arguments.robotConfigPath, jsonDecodeRobotDefinition)
    val components = arguments.componentConfigPaths.map(::loadComponentSourceDefinition)

    if (arguments.arduinoOutputPath != null) {
        val gen = ArduinoCodeGenerator(robot, components)
        gen.buildCompileFolder(arguments.arduinoOutputPath, gen.serialProtocolArduinoLoop)
    }

    if (arguments.kotlinOutputPath != null) {
        val gen = KotlinCodeGenerator(robot, components.map { it.component })
        gen.buildControllerFiles(arguments.kotlinOutputPath)
    }

    if (arguments.staticOutputPath != null) {
        val gen = ArduinoCodeGenerator(robot, components)
        val port = 1234
        val staticRoutine = StaticRoutineReader(port)

        println("Hosting adapter on localhost:$port\nConnect and run commands to generate static routine")

        val b = CyclicBarrier(2)

        staticRoutine.submitted.connect { routine ->
            println("Generating with ${routine.size} commands")
            gen.buildCompileFolder(arguments.staticOutputPath, gen.staticArduinoLoop(routine))
            b.await()
        }

        b.await()
    }
}

class GenerationParameters(
        val robotConfigPath: String,
        val componentConfigPaths: Set<String>,
        val arduinoOutputPath: String?,
        val pythonOutputPath: String?,
        val kotlinOutputPath: String?,
        val staticOutputPath: String?
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
    val genStatic = argValue("-gs")

    if (genPython == null && genKotlin == null && genArduino == null && genStatic == null) cliError("Expected at least one target")
    if (arguments.isEmpty()) cliError("Expected output path")
    if (arguments.size < 2) cliError("Expected robot configuration path")

    return GenerationParameters(
            arguments[0],
            arguments.drop(1).toSet(),
            genArduino,
            genPython,
            genKotlin,
            genStatic
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
