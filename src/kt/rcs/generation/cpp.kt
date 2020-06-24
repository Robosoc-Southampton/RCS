package generation

import definition.RobotComponentDefinition
import definition.RobotProfile
import java.nio.file.Files
import java.nio.file.Paths

fun prepareCPPDirectory(path: String, components: List<RobotComponentDefinition>) {
    Files.createDirectories(Paths.get(path))
    Files.createDirectories(Paths.get(path, "include"))

    val rcsh = Paths.get(path, "include", "rcs.h")
    val rcscpp = Paths.get(path, "rcs.cpp")

    Files.write(rcsh, readLocalResource("/rcs.h").toByteArray())
    Files.write(rcscpp, readLocalResource("/rcs.cpp").toByteArray())

    components.forEach { (name, header, source, _) ->
        val ti = Paths.get(path, "include", "$name.h")
        val ts = Paths.get(path, "$name.cpp")
        if (Files.exists(ti)) Files.delete(ti)
        if (Files.exists(ts)) Files.delete(ts)
        Files.copy(Paths.get(header), ti)
        Files.copy(Paths.get(source), ts)
    }
}

fun generateCPPRobotHandler(
        path: String,
        robot: RobotProfile,
        components: List<RobotComponentDefinition>
) {
    val content = generateCPPContent(robot, components)
    writeFileOverwriting(path, "${robot.name}.ino", content)
}

private fun readLocalResource(resource: String) =
    String(RobotProfile::class.java.getResourceAsStream(resource)!!.readAllBytes())

private fun generateCPPContent(
        robot: RobotProfile,
        components: List<RobotComponentDefinition>
): String {
    val componentLookup = components.map { it.type to it } .toMap()
    val includes = components.joinToString("\n") {
        "#include \"include/${it.type}.h\"" }
    val componentDecls = robot.components.joinToString("\n") {
        "${it.type} ${it.name};" }

    val resolved = resolveComponents(robot.components, componentLookup)
    val offsets = computeMethodOffsets(resolved)

    val maxParameters = resolved
            .map { it.second } .toSet()
            .flatMap { it.methods }
            .map { it.parameters.size }
            .max() ?: 0

    val indent = "            "

    val controlLoopCases = offsets.flatMap { (c, d, base) ->
        d.methods.mapIndexed { i, m ->
            val case = "case ${base + i}:"
            val params = m.parameters.mapIndexed { idx, _ -> "p$idx = rcs::read_serial_int();" }
                    .joinToString("\n$indent")
            val callParams = m.parameters.mapIndexed { idx, _ -> "p$idx" }
                    .joinToString(", ")
            val call = "${if (m.returns) "rv = " else ""}${c.name}.${m.name}($callParams);"
            val response = if (m.returns)
                "rcs::write_serial_int($RET_OPCODE_RETURN);\n${indent}rcs::write_serial_int(rv);"
            else
                "rcs::write_serial_int($RET_OPCODE_NO_RETURN);"

            "$case\n$indent$params\n$indent$call\n$indent$response\n${indent}break;"
        }
    } .joinToString("\n        ")

    val controlLoopParams = "i16 " + (0 until maxParameters).joinToString { "p$it" } + ";"

    return CPP_TEMPLATE
            .replace("%INCLUDES", includes)
            .replace("%COMPONENT_DECLS", componentDecls)
            .replace("%CONTROL_LOOP_PARAMS", controlLoopParams)
            .replace("%CONTROL_LOOP_CASES", controlLoopCases)
}

private const val CPP_TEMPLATE = """
%INCLUDES
#include "include/rcs.h"
#include <Arduino.h>

%COMPONENT_DECLS

// TODO: ISR

void setup() {
    // TODO: setup ISR
    
    Serial.begin(9600);
}

void loop() {
    %CONTROL_LOOP_PARAMS
    i16 rv, p_delay, p_precision, p_l, p_r;
    i16 opcode = rcs::read_serial_int();
    switch (opcode) {
        case $OUT_OPCODE_DELAY:
            p_delay = rcs::read_serial_int();
            delay(p0);
            break;
        %CONTROL_LOOP_CASES
    }
}
"""
