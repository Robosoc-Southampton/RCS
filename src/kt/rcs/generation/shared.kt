package generation

import definition.RobotComponentDefinition
import definition.RobotComponentInstance

typealias ResolvedComponentInstance = Pair<RobotComponentInstance, RobotComponentDefinition>
typealias OffsetRobotComponentInstance = Triple<RobotComponentInstance, RobotComponentDefinition, Int>

const val RET_OPCODE_ERROR = 0
const val RET_OPCODE_NO_RETURN = 1
const val RET_OPCODE_RETURN = 2

const val OUT_OPCODE_DELAY = 0
const val OUT_OPCODE_MOVE = 1
const val OUT_OPCODE_METHOD_BASE = 8

fun resolveComponents(
        components: List<RobotComponentInstance>,
        componentLookup: Map<String, RobotComponentDefinition>
): List<ResolvedComponentInstance> {
    return components.map {
        it to (componentLookup[it.type] ?: error("No such component '${it.type}'"))
    }
}

fun computeMethodOffsets(
        components: List<ResolvedComponentInstance>
): List<OffsetRobotComponentInstance> {
    val initial = listOf<OffsetRobotComponentInstance>() to OUT_OPCODE_METHOD_BASE
    return components.fold(initial) { (l, n), (c, d) ->
        l + listOf(Triple(c, d, n)) to n + d.methods.size
    } .first
}
