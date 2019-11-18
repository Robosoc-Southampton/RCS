import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import rcs.angle
import rcs.minus
import kotlin.math.min

class EnvironmentRenderer(
        width: Double,
        height: Double,
        private val context: GraphicsContext,
        private val environment: EnvironmentConfiguration
) {
    init {
        val envSize = environment.upperRightCorner - environment.lowerLeftCorner
        val scale = min(width / envSize.dx, height / envSize.dy)

        context.fill = Color.BLACK
        context.fillRect(0.0, 0.0, width, height)

        context.translate(0.0, height)
        context.scale(1.0, -1.0)
        context.translate(
                (width - scale * envSize.dx) / 2,
                (height - scale * envSize.dy) / 2
        )
        context.scale(scale, scale)
        context.translate(
                -environment.lowerLeftCorner.x.toDouble(),
                -environment.lowerLeftCorner.y.toDouble()
        )

        context.fill = Color.WHITE
        context.fillRect(
                environment.lowerLeftCorner.x.toDouble(),
                environment.lowerLeftCorner.y.toDouble(),
                envSize.dx.toDouble(),
                envSize.dy.toDouble()
        )
    }

    fun draw(robot: SimulatedRobot) {
        val location = robot.position.location
        val direction = robot.position.direction

        robot.configuration.shapes.sortedBy(Shape::elevation).forEach { shape ->
            var colour = robot.instance.tint.toColor()
            repeat(shape.elevation) { colour = colour.darker() }

            context.save()
            context.fill = colour
            context.translate(location.x.toDouble(), location.y.toDouble())
            context.rotate(direction.angle.toDouble())
            context.translate(shape.position.x.toDouble(), shape.position.y.toDouble())

            val (w, h, f) = when (shape) {
                is RectangleShape -> {
                    Triple(shape.width.toDouble(), shape.height.toDouble(), context::fillRect)
                }
                is CircleShape -> {
                    Triple(shape.radius * 2.0, shape.radius * 2.0, context::fillOval)
                }
            }

            f(-w/2, -h/2, w, h)

            context.restore()
        }
    }

    fun draw(robots: List<SimulatedRobot>) = robots.forEach(this::draw)
}
