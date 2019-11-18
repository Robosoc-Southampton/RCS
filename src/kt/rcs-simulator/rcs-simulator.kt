import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import rcs.*
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

        val robots = RobotConfiguration(
                1234,
                listOf(
                        CircleShape(PositionVec2D(0.mm), 10.cm, 0),
                        RectangleShape(PositionVec2D(-8.cm, 0.cm), 1.cm, 4.cm, 1),
                        RectangleShape(PositionVec2D(8.cm, 0.cm), 1.cm, 4.cm, 1)
                ),
                RobotPosition(PositionVec2D(1.m), DirectionVec2D(1.m).normalise()),
                listOf(
                        RobotInstance(ErrorRange.LOW, ErrorRange.HIGH, Colour.colours["blue"]!!),
                        RobotInstance(ErrorRange.DEFAULT, ErrorRange.ZERO, Colour.colours["orange"]!!)
                )
        ).getSimulatedRobots()

        println(robots[0].position.direction.angle)

        val environment = EnvironmentConfiguration(
                PositionVec2D(0.mm, 0.mm),
                PositionVec2D(3.m, 2.m)
        )

        Thread {
            while (true) {
                sim.draw(environment) { robots.forEach(this::draw) }
                Thread.sleep(20)
                robots.forEach {
                    it.forward(1.cm)
                    it.turn(-1.deg)
                }
            }
        } .start()
    }
}

fun main(args: Array<String>) {
    launch(RCSSimulator::class.java, *args)
}
