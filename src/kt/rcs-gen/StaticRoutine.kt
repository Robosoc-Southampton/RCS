import rcs.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class StaticRoutine(val instructions: List<StaticRoutineInstruction>)

// TODO: take robot and validate method calls
fun loadStaticRoutine(file: String): StaticRoutine {
    val lines = Files.lines(Paths.get(file))

    return lines.toList().mapIndexed { n, l ->
        parseStaticRoutineInstructionLine(n + 1, l)
    } .filterNotNull().let(::StaticRoutine)
}

private fun parseStaticRoutineInstructionLine(n: Int, line: String): StaticRoutineInstruction? {
    val parts = line
            .replace(Regex("//.*"), "")
            .trim()
            .split(" ")
            .takeIf { it.isNotEmpty() }
            ?: return null

    return when (parts[0]) {
        "wait" -> {
            val delay = parts.getOrNull(1)
                    ?: throw StaticRoutineFileError("Expected delay after 'wait'", n)

            return StaticRoutineWait(parseWithUnits(delay,
                    "ms" to Float::ms,
                    "s" to Float::s))
        }
        "forward" -> {
            val distance = parts.getOrNull(1)
                    ?: throw StaticRoutineFileError("Expected distance after 'forward'", n)

            return StaticRoutineForward(parseWithUnits(distance,
                    "mm" to Float::mm,
                    "cm" to Float::cm,
                    "m" to Float::m))
        }
        "turn" -> {
            val angle = parts.getOrNull(1)
                    ?: throw StaticRoutineFileError("Expected angle after 'turn'", n)

            return StaticRoutineTurn(parseWithUnits(angle,
                    "deg" to Float::deg))
        }
        else -> {
            if (!parts[0].contains("."))
                throw StaticRoutineFileError("Expected <component>.<method>", n)

            val (component, method) = parts[0].split(".")
            val parameters: List<ComponentValue> = parts.drop(1).map(String::toInt)

            return StaticRoutineCall(component, method, parameters)
        }
    }
}

private fun parseWithUnits(item: String, vararg units: Pair<String, (Float) -> Float>): Float {
    units.forEach { (unit, fn) ->
        if (item.endsWith(unit)) {
            val n = item.removeSuffix(unit).toFloat()
            return fn(n)
        }
    }

    return item.toFloat()
}

class StaticRoutineFileError(message: String, line: Int): Throwable() {
    override val message: String = "$message on line $line"
}

sealed class StaticRoutineInstruction
data class StaticRoutineWait(val delay: Milliseconds): StaticRoutineInstruction()
data class StaticRoutineForward(val distance: Millimetres): StaticRoutineInstruction()
data class StaticRoutineTurn(val angle: Degrees): StaticRoutineInstruction()
data class StaticRoutineCall(val component: String, val method: String, val parameters: List<ComponentValue>): StaticRoutineInstruction()
