import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

class SimulatorView: Canvas(WIDTH, HEIGHT) {
    fun draw(environment: EnvironmentConfiguration, fn: EnvironmentRenderer.() -> Unit) {
        context.save()
        fn(EnvironmentRenderer(width, height, context, environment))
        context.restore()
    }

    fun visualiseColours() {
        context.fill = Color.FLORALWHITE
        context.fill()
        context.fill = Color.AQUAMARINE
        context.fillRect(100.0, 100.0, 200.0, 200.0)

        val w = WIDTH / Colour.colours.size * 3

        Colour.colours.values.forEachIndexed { i, colour ->
            val x = (i % 7) * w
            val y = (i / 7).toDouble() * 100
            context.fill = colour.toColor()
            context.fillRect(x, y, w, 100.0)
            context.fill = Color.WHITE
            context.fillText(Colour.colours.entries.first { it.value == colour } .key,
                    x + 10, y + 10)
        }
    }

    private val context = graphicsContext2D!!

    companion object {
        const val WIDTH = 1080.0
        const val HEIGHT = 720.0
    }
}
