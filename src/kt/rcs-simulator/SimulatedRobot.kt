import rcs.Degrees
import rcs.Millimetres
import rcs.forward
import rcs.rotate

class SimulatedRobot(
        val configuration: RobotConfiguration,
        val instance: RobotInstance
) {
    var position = configuration.spawn

    fun forward(distance: Millimetres) {
        position = position.forward(instance.forwardError.apply(distance))
    }

    fun turn(angle: Degrees) {
        position = position.rotate(instance.turnError.apply(angle))
    }
}
