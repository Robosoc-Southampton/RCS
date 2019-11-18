import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import rcs.util.ArgumentParser

val parser = ArgumentParser.create("rcs-simulator") {
    switch("robot configuration", "-r", null,
            optional = true,
            description = "Configuration files for robots to add initially")

    switch("environment", "-e", 1,
            optional = true,
            description = "File containing environment obstacles and layout")
}

class RCSSimulator: Application() {
    override fun start(stage: Stage) {
        val arguments = parser.parse(parameters.raw.toTypedArray())
        val robotConfigurations = arguments.optionalValues("robot configuration")
        val environmentFile = arguments.optionalValue("environment")
        val sim = SimulatorView()

        stage.scene = Scene(StackPane().apply { children.add(sim) }, SimulatorView.WIDTH, SimulatorView.HEIGHT)
        stage.title = "rcs-simulator"
        stage.show()
    }
}

fun main(args: Array<String>) {
    launch(RCSSimulator::class.java, *args)
}
