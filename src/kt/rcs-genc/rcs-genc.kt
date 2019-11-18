import rcs.definition.RCSComponentInfo
import rcs.definition.RCSRobotInfo
import rcs.loadComponentDefinition
import rcs.loadRobotConfiguration
import rcs.util.ArgumentParser
import rcs.util.toInfo
import rcs.util.toInfoSet
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgumentParser.create("rcs-genc") {
        switch("language", "-l", 1,
                optional = false,
                description = "Language to generate controller code in ('kotlin'|'python')")

        switch("output path", "-o", 1,
                optional = false,
                description = "Folder to place generated code in")

        switch("robot", "-r", 1,
                optional = false,
                description = "Path to robot configuration file (JSON)")

        flag("no components", "-nc",
                description = "Pass this flag to not generate component files")

        switch("components", "-c", null,
                description = "Paths to component definitions")
    }
    val arguments = parser.parse(args)
    val language = arguments.value("language")
    val outputPath = arguments.value("output path")

    if (language != "kotlin" && language != "python") {
        System.err.println("Expected language to be one of 'kotlin' or 'python'")
        parser.printUsage()
        exitProcess(1)
    }

    val robot = loadRobotConfiguration(arguments.value("robot"))
    val components = arguments.values("components").map(::loadComponentDefinition)
    val componentSet = components.map { it.configuration } .toSet().toInfoSet()
    val controllerGenerator = getControllerGenerator(language, robot.toInfo(componentSet))

    controllerGenerator.generate(outputPath)

    if (!arguments.flag("no components")) {
        val componentGenerators = components.map {
            getComponentGenerator(language, it.configuration.toInfo())
        }

        componentGenerators.forEach { it.generate("$outputPath/component") }
    }
}

fun getControllerGenerator(language: String, robot: RCSRobotInfo) = when (language) {
    "kotlin" -> KotlinControllerGenerator(robot)
    "python" -> PythonControllerGenerator(robot)
    else -> error("")
}

fun getComponentGenerator(language: String, component: RCSComponentInfo) = when (language) {
    "kotlin" -> KotlinComponentGenerator(component)
    "python" -> PythonComponentGenerator(component)
    else -> error("")
}
