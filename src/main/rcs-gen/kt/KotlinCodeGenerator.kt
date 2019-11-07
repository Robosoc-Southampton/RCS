package kt

import ComponentDefinition
import RobotDefinition
import java.nio.file.Files
import java.nio.file.Paths

class KotlinCodeGenerator(
        private val robot: RobotDefinition,
        private val components: List<ComponentDefinition>
) {
    fun buildControllerFiles(folder: String) {
        buildRobot("$folder/${robot.name}.kt")

        components.forEach {
            buildComponent(it, "$folder/components/${it.name}.kt")
        }
    }

    private fun buildComponent(component: ComponentDefinition, file: String) {
        val builder = KotlinCodeBuilder().apply {
            writeClass(camelCase(component.name)) {
                parameter("controller", "ControlHandle")
                parameter("componentName", "String")
                parameter("componentIndex", "Int")

                component.methods.forEachIndexed { index, dmethod ->
                    method(dmethod.name, if (dmethod.returns) "ComponentValue" else "Unit",
                            *dmethod.parameters.map { it to "ComponentValue" }.toTypedArray()) {
                        writeLine("controller.call(" +
                                "componentName, componentIndex, " +
                                "\"${dmethod.name}\", $index, " +
                                "listOf(${dmethod.parameters.joinToString(", ")}))")

                        if (dmethod.returns) writeLine("return 0 // TODO")
                    }
                }
            }
        }

        writeFile(file, builder.toString())
    }

    private fun buildRobot(file: String) {
        val builder = KotlinCodeBuilder().apply {
            imports("java.net.InetAddress")
            imports("java.net.Socket")

            writeClass(robotClassName, "ControlHandle()") {
                parameter("addr", "String", false)
                parameter("port", "Int", false)

                robot.components.forEachIndexed { index, component ->
                    field(component.name, camelCase(component.type) +
                            "(this, \"${component.name}\", $index)", true)
                }

                mutableField("_connection", "null as Socket?")

                overrideMethod("connect", "Unit") {
                    writeLine("if (_connection == null)\n" +
                            "\t_connection = Socket(InetAddress.getByName(addr), port)")
                }

                overrideMethod("disconnect", "Unit") {
                    writeLine("val c = _connection")
                    block("if (c != null)") {
                        block("synchronized(c)") {
                            writeLine("c.close()")
                            writeLine("disconnected.emit(false)")
                        }
                    }
                }

                overrideMethod("wait", "Unit",
                        "delay" to "Milliseconds",
                        "fn" to "WaitCommandHandle.() -> Unit") {

                }

                overrideMethod("forward", "Unit",
                        "distance" to "Millimetres",
                        "fn" to "ForwardCommandHandle.() -> Unit") {

                }

                overrideMethod("turn", "Unit",
                        "angle" to "Degrees",
                        "fn" to "TurnCommandHandle.() -> Unit") {

                }

                overrideMethod("call", "Unit",
                        "component" to "ComponentID",
                        "componentIndex" to "Int",
                        "method" to "MethodID",
                        "methodIndex" to "Int",
                        "parameters" to "List<ComponentValue>",
                        "fn" to "CallCommandHandle.() -> Unit") {
                }
            }
        }

        writeFile(file, builder.toString())
    }

    private fun writeFile(file: String, content: String) {
        Files.write(Paths.get(file), content.toByteArray())
    }

    private val robotClassName
            = camelCase(robot.name) + "Controller"

    private fun camelCase(name: String): String {
        return (name.substring(0, 1).toUpperCase() + name.substring(1))
                .replace(Regex("-(.)")) { res -> res.groupValues[1].toUpperCase() }
    }
}
