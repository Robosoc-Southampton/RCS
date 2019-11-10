package cpp

import CALL_INSTRUCTION
import CallCommand
import Command
import CompilationError
import ComponentDefinition
import ComponentID
import ForwardCommand
import MAX_METHODS_PER_COMPONENT
import RobotDefinition
import TurnCommand
import WaitCommand
import java.nio.file.Files
import java.nio.file.Paths

/** TODO: Robot configuration e.g. wheel radius, wheel separation
 *        Motor controller usage code generation
 *        Collision avoidance code generation
 */
class ArduinoCodeGenerator(
        private val robot: RobotDefinition,
        private val components: List<ComponentSourceDefinition>
) {
    fun buildCompileFolder(folder: String, loopBodyBuilder: CPPBlockBuilder.() -> Unit) {
        val generator = CPPCodeBuilder()

        makeFolder(folder)
        makeFolder("$folder/arduino")
        makeFolder("$folder/arduino/include")

        components.forEach { (c, header, source) ->
            writeFile("$folder/arduino/include/component_${c.name}.h", header)
            generator.includes("include/component_${c.name}")
            writeFile("$folder/arduino/component_${c.name}.cpp",
                    "#include \"include/component_${c.name}.h\"\n$source")
        }

        writeMainFile(generator, loopBodyBuilder)
        writeFile("$folder/arduino/arduino.ino", generator.toString())
    }

    fun staticArduinoLoop(routine: List<Command>): CPPBlockBuilder.() -> Unit = {
        // TODO: actually fix this
        //  values are floats and need to be rounded to ints
        routine.forEach {when(val cmd = it) {
            is WaitCommand -> statement("delay(${cmd.delay})")
            is ForwardCommand -> statement("forward(${cmd.distance})")
            is TurnCommand -> statement("turn(${cmd.angle})")
            is CallCommand -> statement("${cmd.component}.${cmd.method}(${cmd.parameters.joinToString()})")
        } }
    }

    val serialProtocolArduinoLoop: CPPBlockBuilder.() -> Unit = {
        statement("if (Serial.available() < 2) return")
        statement("int getLength = readInteger()")
        statement("while (Serial.available() < getLength)")
        statement("int opcode = readInteger()")

        block("if (opcode == $CALL_INSTRUCTION)") {
            statement("int idx = readInteger()")

            robot.components.forEachIndexed { i, component ->
                val componentType = componentTypeLookup[component.type]!!
                val methods = componentType.methods

                methods.forEachIndexed { mi, method ->
                    val idx = i * MAX_METHODS_PER_COMPONENT + mi

                    method.parameters.forEach {
                        statement("int16_t p_${idx}_$it")
                    }
                }
            }

            block("switch (idx)") {
                robot.components.forEachIndexed { i, component ->
                    val componentType = componentTypeLookup[component.type]!!
                    val methods = componentType.methods

                    methods.forEachIndexed { mi, method ->
                        val idx = i * MAX_METHODS_PER_COMPONENT + mi
                        writeLine("case $idx:")

                        method.parameters.forEach {
                            statement("p_${idx}_$it = readInteger()")
                        }

                        if (method.returns) {

                        }
                        else {
                            statement("${component.name}.${method.name}(" +
                                    method.parameters.map { "p_${idx}_$it" } .joinToString() + ")")
                        }

                        statement("break")
                    }
                }
            }
        }
    }

    private fun writeMainFile(generator: CPPCodeBuilder, loopBodyBuilder: CPPBlockBuilder.() -> Unit) {
        robot.components.forEach {
            generator.global(it.name, it.type)
        }

        generator.function("readInteger", "int16_t") {
            statement("auto b0 = Serial.read()")
            statement("auto b1 = Serial.read()")
            statement("return b0 << 8 | b1")
        }

        generator.setup {
            statement("Serial.begin(9600)")
        }

        generator.setup {
            robot.components.forEach {
                val comp = componentTypeLookup[it.type]
                        ?: throw CompilationError("No such component type '${it.type}'")
                val params = comp.attributes.map { def ->
                    it.attributes.firstOrNull { it.name == def.name } ?.value ?: def.defaultValue
                        ?: throw CompilationError("No value specified for attribute '${it.name}'")
                }

                statement("${it.name}.setup(${params.joinToString(", ")})")
            }
        }

        generator.loop(loopBodyBuilder)
    }

    private fun makeFolder(folder: String) {
        val path = Paths.get(folder)
        // if (Files.exists(path)) Files.delete(path)
        Files.createDirectories(path)
    }

    private fun writeFile(file: String, content: String) {
        Files.write(Paths.get(file), content.toByteArray())
    }

    private val componentTypeLookup: Map<ComponentID, ComponentDefinition>
            = components.map { it.component.name to it.component } .toMap()
}
