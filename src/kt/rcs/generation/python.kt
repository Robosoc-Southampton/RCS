package generation

import definition.RobotComponentDefinition
import definition.RobotProfile

fun generatePython(
        path: String,
        robot: RobotProfile,
        components: List<RobotComponentDefinition>
) {
    val rname = robot.name.split("-").joinToString("") { it[0].toUpperCase() + it.substring(1) }
    val componentLookup = components.map { it.type to it } .toMap()
    val resolved = resolveComponents(robot.components, componentLookup)
    val offset = computeMethodOffsets(resolved)

    components.forEach { generateComponent(path, it) }

    val constructor = "self.__writer = writer\n" + offset
            .joinToString("\n") { (inst, _, offset) ->
                "self.${inst.name} = ${inst.type}(self.__writer, $offset)"
            }

    val imports = robot.components.map { it.type } .toSet() .joinToString("") {
        "from $it import $it\n"
    }

    val content = imports + "class ${rname}:\n    " +
            "def __init__(self, writer):\n        " + constructor.replace("\n", "\n        ")

    writeFileOverwriting(path, rname + ".py", content)
}

fun generateComponent(path: String, component: RobotComponentDefinition) {
    val rest = component.methods.mapIndexed { i, it ->
        val paramStrings1 = listOf("self") + it.parameters.map {
            "${it.name}${if (it.defaultValue != null) " = " + it.defaultValue else ""}"
        }
        val paramStrings2 = listOf("self.__base + $i") + it.parameters.map { it.name }
        "def ${it.name}(${paramStrings1.joinToString(", ")}):" +
                "\n     return self.__writer.send(${paramStrings2.joinToString(", ")})"
    }.joinToString("\n\n")

    val content = "class ${component.type}:\n    " +
            "${init.replace("\n", "\n    ")}\n\n    " +
            rest.replace("\n", "\n    ")

    writeFileOverwriting(path, component.type + ".py", content)
}

private const val init = """
def __init__(self, writer, base: int):
    self.__base = base
    self.__writer = writer
"""
