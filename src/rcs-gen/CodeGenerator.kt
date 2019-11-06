import java.nio.file.Files
import java.nio.file.Paths

class CodeGenerator(
        private val robot: RobotDefinition,
        private val components: List<ComponentSourceDefinition>
) {
    fun buildCompileFolder(folder: String) {
        val generator = CPPGenerator()

        makeFolder(folder)
        makeFolder("$folder/robot")
        makeFolder("$folder/robot/include")

        components.forEach { (c, header, source) ->
            writeFile("$folder/robot/include/component_${c.name}.h", header)
            generator.includes("include/component_${c.name}")
            writeFile("$folder/robot/component_${c.name}.cpp",
                    "#include \"include/component_${c.name}.h\"\n$source")
        }

        writeMainFile(generator)
        writeFile("$folder/robot/robot.ino", generator.toString())
    }

    private fun writeMainFile(generator: CPPGenerator) {
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

        generator.loop {
            statement("if (Serial.available() < 2) return")
            statement("int length = readInteger()")
            statement("while (Serial.available() < length)")
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
    }

    private fun makeFolder(folder: String) {
        val path = Paths.get(folder)
//        if (Files.exists(path)) Files.delete(path)
        Files.createDirectories(path)
    }

    private fun writeFile(file: String, content: String) {
        Files.write(Paths.get(file), content.toByteArray())
    }

    private val componentTypeLookup: Map<ComponentID, ComponentDefinition>
            = components.map { it.component.name to it.component } .toMap()
}
