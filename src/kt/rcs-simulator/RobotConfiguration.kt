import javafx.scene.paint.Color
import rcs.Millimetres
import rcs.PositionVec2D
import rcs.RobotPosition

data class RobotConfiguration(
        val port: Int,
        val shapes: List<Shape>,
        val spawn: RobotPosition,
        val instances: List<RobotInstance>
)

sealed class Shape(val position: PositionVec2D, val elevation: Int = 0)

class RectangleShape(
        position: PositionVec2D,
        val width: Millimetres, val height: Millimetres,
        elevation: Int = 0
): Shape(position, elevation)

class CircleShape(
        position: PositionVec2D,
        val radius: Millimetres,
        elevation: Int = 0
): Shape(position, elevation)

data class RobotInstance(
        val forwardError: ErrorRange,
        val turnError: ErrorRange,
        val tint: Colour
)

data class ErrorRange(
        val min: Float,
        val max: Float = min
) {
    fun apply(v: Float) = (min + Math.random() * (max - min)).toFloat() * v

    companion object {
        val LOW = ErrorRange(0.97f)
        val HIGH = ErrorRange(1.03f)
        val ZERO = ErrorRange(1f)
        val DEFAULT = ErrorRange(0.97f, 1.03f)
    }
}

data class Colour(val r: Int, val g: Int, val b: Int) {
    fun toColor() = Color.color(r / 255.0, g / 255.0, b / 255.0)

    companion object {
        val colours = mapOf(
                "indigo" to Colour(70, 70, 140),
                "1" to Colour(70, 70, 210),
                "teal" to Colour(70, 140, 140),
                "blue" to Colour(70, 140, 210),
                "green" to Colour(70, 210, 140),
                "cyan" to Colour(70, 210, 210),

                "burgundy" to Colour(140, 70, 70),
                "plum" to Colour(140, 70, 140),
                "purple" to Colour(140, 70, 210),
                "violet" to Colour(140, 140, 210),
                "lime" to Colour(140, 210, 110),
                "sky" to Colour(140, 210, 210),

                "red" to Colour(210, 70, 70),
                "magenta" to Colour(210, 70, 140),
                "pink" to Colour(210, 70, 210),
                "orange" to Colour(210, 120, 50),
                "peach" to Colour(210, 140, 140),
                "lilac" to Colour(210, 140, 210),
                "yellow" to Colour(230, 230, 50)
        )
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

fun RobotConfiguration.getSimulatedRobots()
        = instances.map { SimulatedRobot(this, it) }
