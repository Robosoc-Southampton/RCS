import rcs.loadComponentDefinition
import rcs.loadRobotConfiguration
import rcs.util.ArgumentParser
import rcs.util.toInfo
import rcs.util.toInfoSet

fun main(args: Array<String>) {
    val parser = ArgumentParser.create("rcs-gen") {
        switch("static routine", "-s", 1,
                optional = true,
                description = "Path to static routine file")

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
    val outputPath = arguments.value("output path")
    val staticRoutinePath = arguments.optionalValue("static routine")
    val components = arguments.values("components").map(::loadComponentDefinition)
    val componentSet = components.map { it.configuration} .toSet().toInfoSet()
    val robot = loadRobotConfiguration(arguments.value("robot"))
    val generator = ArduinoCodeGenerator(robot.toInfo(componentSet), components.toSet())

    generator.generateArduinoFile(outputPath, staticRoutinePath?.let(::loadStaticRoutine))

    if (!arguments.flag("no components"))
        generator.generateComponentFiles(outputPath)
}
