import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

class SimulatorView: Canvas(WIDTH, HEIGHT) {
    private val context = graphicsContext2D!!

    init {
        context.fill = Color.FLORALWHITE
        context.fill()
        context.fill = Color.AQUAMARINE
        context.fillRect(100.0, 100.0, 200.0, 200.0)
    }

    companion object {
        const val WIDTH = 1080.0
        const val HEIGHT = 720.0
    }
}
