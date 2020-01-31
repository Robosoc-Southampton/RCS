package generation

import definition.RobotComponentDefinition
import definition.RobotProfile
import java.nio.file.Files
import java.nio.file.Paths

fun prepareCPPDirectory(path: String, components: List<RobotComponentDefinition>) {
    Files.createDirectories(Paths.get(path))
    Files.createDirectories(Paths.get(path, "include"))

    components.forEach { (name, header, source, _, _) ->
        Files.copy(Paths.get(header), Paths.get(path, "include", "$name.h"))
        Files.copy(Paths.get(source), Paths.get(path, "$name.cpp"))
    }
}

fun generateCPPRobotHandler(path: String, robot: RobotProfile) {
    Files.write(Paths.get(path, "${robot.name}.ino"), "".toByteArray())
}
