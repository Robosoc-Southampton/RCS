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

                mutableField("_adapter", "null as AdapterClient?")

                method("send", "Unit",
                        "data" to "Command") {
                    writeLine("_adapter?.send(data)")
                }

                overrideMethod("connect", "Unit") {
                    block("if (_adapter == null)") {
                        writeLine("val socket = Socket(InetAddress.getByName(addr), port)")
                        writeLine("val adapter = AdapterClient(socket)")
                        writeLine("_adapter = adapter")

                        block("adapter.disconnected.connect") {
                            writeLine("disconnected.emit(false)")
                            writeLine("if (adapter == _adapter) _adapter = null")
                        }

                        block("adapter.commandSubmitted.connect") {
                            block("when (it)") {
                                writeLine("is InfoCommand -> info.emit(it.info)")
                                writeLine("is DisconnectCommand -> disconnected.emit(true)")
                                writeLine("is PositionCommand -> position.value(it.position)")
                            }

                            writeLine("commandSubmitted.emit(it)")
                        }
                    }
                }

                overrideMethod("disconnect", "Unit") {
                    writeLine("_adapter?.disconnect()")
                }

                overrideMethod("wait", "Unit",
                        "delay" to "Milliseconds") {
                    writeLine("send(WaitCommand(delay))")
                }

                overrideMethod("forward", "Unit",
                        "distance" to "Millimetres") {
                    writeLine("send(ForwardCommand(distance))")
                }

                overrideMethod("turn", "Unit",
                        "angle" to "Degrees") {
                    writeLine("send(TurnCommand(angle))")
                }

                overrideMethod("call", "Unit",
                        "component" to "ComponentID",
                        "componentIndex" to "Int",
                        "method" to "MethodID",
                        "methodIndex" to "Int",
                        "parameters" to "List<ComponentValue>") {
                    writeLine("send(CallCommand(component, componentIndex, method, methodIndex, parameters))")
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
