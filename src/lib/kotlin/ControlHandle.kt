import observables.Observable
import observables.Signal

/**
 * A control handle is an abstraction of an entity's control of a robot.
 *
 * There exist methods to move the robot, control its components, tell it to
 * wait, and set its position (i.e. tell the control handle where the robot is).
 *
 * In addition, all commands submitted to the robot will be emitted in a signal,
 * allowing distributed control of a robot. Commands will also receive status
 * signals throughout execution.
 *
 * Finally, events may be triggered by the robot, which will be received by the
 * control handle, which can also return a value in response to the event.
 *
 * A ControlHandle has an observable position variable, tracking the robot's
 * current position. This may be set at any time to update the robot's position.
 *
 * A ControlHandle has 3 signals:
 *  * commandSubmitted(CommandHandle)
 *  * info(String)
 *  * disconnected(Boolean)
 *
 * Along with 4 command methods:
 *  * wait(Milliseconds)
 *  * forward(Millimetres)
 *  * turn(Degrees)
 *  * call(ComponentID, MethodID, ComponentValue...)
 */
abstract class ControlHandle {
    /** Signal emitted when a command is submitted to the robot, not necessarily
     *  by this control handle. */
    val commandSubmitted = Signal<Command>()

    /** Signal emitted when arbitrary non-functional information is fed back by
     *  the robot. */
    val info = Signal<String>()

    /** Signal emitted when the connection is lost, with a parameter indicating
     *  whether the remote connection was lost or the direct one. I.e., if true,
     *  the bluetooth connection may have dropped, but if false, the TCP socket
     *  may have been closed. */
    val disconnected = Signal<Boolean>()

    /** The current location of the robot being controlled.
     *  Connect to position.changed for position updates. */
    val position = Observable(RobotPosition(Location()))

    /** Connect to the robot. Will attempt to re-establish the remote connection
     *  if already connected to an adapter. */
    abstract fun connect()

    /** Disconnect from the robot. Will emit the disconnected signal. */
    abstract fun disconnect()

    /** Wait by the specified time in milliseconds. */
    abstract fun wait(delay: Milliseconds)

    /** Move the robot forward by `distance`. */
    abstract fun forward(distance: Millimetres)

    /** Turn the robot `angle` degrees counter-clockwise. */
    abstract fun turn(angle: Degrees)

    /** Call a method on a component on the robot, returning anything it returns
     *  if applicable. */
    abstract fun call(
            component: ComponentID,
            componentIndex: Int,
            method: MethodID,
            methodIndex: Int,
            parameters: List<ComponentValue>
    )
}
