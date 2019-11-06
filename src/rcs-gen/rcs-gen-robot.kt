import java.nio.file.Files
import java.nio.file.Paths

const val USAGE = """
Usage: rcs-gen-robot <output> <robot> <component-configuration...>
Example usage: rcs-gen-robot src/arduino config/robot.json config/component/L298
"""

fun main(args: Array<String>) {
    if (args.size < 2) {
        System.err.println(USAGE)
        return
    }

    val outputPath = args[0]
    val robotPath = args[1]
    val componentPaths = args.drop(2)
    val robot = readJSONFile(robotPath, jsonDecodeRobotDefinition)
    val components = componentPaths.map {
        val config = readJSONFile("$it/config.json", jsonDecodeComponentDefinition)
        val header = readFile("$it/header.h")
        val source = readFile("$it/source.cpp")

        ComponentSourceDefinition(config, header, source)
    }

    CodeGenerator(robot, components).buildCompileFolder(outputPath)
}

private fun readFile(file: String)
        = String(Files.readAllBytes(Paths.get(file)))

private fun <T> readJSONFile(file: String, decoder: JSONDecoder<T>)
        = decoder(jsonParse(readFile(file)))
