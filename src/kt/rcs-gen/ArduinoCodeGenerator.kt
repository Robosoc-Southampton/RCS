import rcs.Degrees
import rcs.Millimetres
import rcs.definition.RCSComponentDefinition
import rcs.definition.RCSRobotInfo
import rcs.util.writeFile

class ArduinoCodeGenerator(
        private val robot: RCSRobotInfo,
        private val components: Set<RCSComponentDefinition>
) {
    fun generateArduinoFile(path: String, staticRoutine: StaticRoutine?) {
        writeFile("$path/arduino/arduino.ino", getFileContent(staticRoutine))
    }

    fun generateComponentFiles(path: String) {
        components.forEach {
            val name = it.configuration.name

            writeFile("$path/arduino/component_$name.cpp",
                    "#include \"include/component_$name.h\"" + it.source)
            writeFile("$path/arduino/include/component_$name.h", it.header)
        }
    }

    private fun getFileContent(staticRoutine: StaticRoutine?) = CODE_TEMPLATE
            .replace("%INCLUDES", getIncludes())
            .replace("%SETUP_CODE", getSetupCode())
            .replace("%RUN_CODE", staticRoutine?.let(::getStaticCode)
                    ?: getDynamicCode())

    private fun getIncludes() = robot.components
            .map { it.component }
            .toSet()
            .joinToString("\n") { "#include \"include/component_${it.name}.h\"" }

    private fun getSetupCode() = robot.components.joinToString("\n\t") {
        val attrs = it.component.attributes.map { attr ->
            it.attributes.firstOrNull { it.name == attr.name }
                    ?: attr.defaultValue
                    ?: error("no value for parameter '${attr.name}'")
        }

        it.name + ".setup(" + attrs.joinToString() + ");"
    }

    private fun getStaticCode(routine: StaticRoutine): String {
        return routine.instructions.joinToString("\n\t") { when (it) {
            is StaticRoutineWait -> "delay(${it.delay.toInt()});"
            is StaticRoutineForward -> "forward(${convertStaticDistance(it.distance)});"
            is StaticRoutineTurn -> "turn(${convertStaticAngle(it.angle)});"
            is StaticRoutineCall -> "${it.component}.${it.method}" +
                    "(${it.parameters.joinToString()});"
        } }
    }

    // TODO: convert distance to ticks
    private fun convertStaticDistance(distance: Millimetres) = distance.toInt()

    // TODO: convert angle to distance to ticks
    private fun convertStaticAngle(angle: Degrees) = angle.toInt()

    private fun getDynamicCode(): String {
        TODO("not implemented")
    }
}

private const val CODE_TEMPLATE = """
%INCLUDES

volatile bool rcs_moving;
int16_t rcs_left_encoder_target, rcs_right_encoder_target;

void setEncoderTargets(int16_t left, int16_t right) {
    rcs_moving = true;
    rcs_left_encoder_target = left;
    rcs_right_encoder_target = right;
}

void forward(int16_t ticks) {
    setEncoderTargets(ticks, ticks);
}

void turn(int16_t ticks) {
    setEncoderTargets(-ticks, ticks);
}

void setup() {
	TCCR2B = (TCCR2B & 0xF8) | 0x06;
	TIMSK2 = (TIMSK2 & 0xF9) | 0x02;
	OCR2A = 255;

    Serial.begin(9600);

    %SETUP_CODE
}

void loop() {
    %RUN_CODE
}

ISR(TIMER2_COMPA_vect) {
	
}
"""
