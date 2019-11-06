import observables.Observable
import observables.Signal

// TODO: control handles should have a connect and disconnect() ?

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
 * A ControlHandle has 2 signals:
 *  * commandSubmitted(CommandHandle)
 *  * info(String)
 *
 * Along with 4 command methods:
 *  * wait(Milliseconds)
 *  * forward(Millimetres)
 *  * turn(Degrees)
 *  * call(ComponentID, MethodID, ComponentValue...)
 *
 * Implementing a control handle requires:
 * * Implementing the command methods (these should update position correctly).
 * * Handling position updates properly.
 * * Emitting commandSubmitted when appropriate.
 * * Emitting info when appropriate.
 * * Updating command handle feedback.
 */
abstract class ControlHandle {
    /** Signal emitted when a command is submitted to the robot, not necessarily
     *  by this control handle. */
    val commandSubmitted = Signal<CommandHandle>()

    /** Signal emitted when arbitrary non-functional information is fed back by
     *  the robot. */
    val info = Signal<String>()

    /** The current location of the robot being controlled.
     *  Connect to position.changed for position updates. */
    val position = Observable(RobotPosition(Location()))

    /** Wait by the specified time in milliseconds. */
    abstract fun wait(
            delay: Milliseconds, fn: WaitCommandHandle.() -> Unit = {})

    /** Move the robot forward by `distance`. */
    abstract fun forward(
            distance: Millimetres, fn: ForwardCommandHandle.() -> Unit = {})

    /** Turn the robot `angle` degrees counter-clockwise. */
    abstract fun turn(angle: Degrees, fn: TurnCommandHandle.() -> Unit = {})

    /** Call a method on a component on the robot, returning anything it returns
     *  if applicable. */
    abstract fun call(
            component: ComponentID,
            method: MethodID,
            vararg parameters: ComponentValue,
            fn: CallCommandHandle.() -> Unit = {}
    )

    /** Call a method on a component on the robot, returning anything it returns
     *  if applicable. */
    fun call(
            component: ComponentID,
            method: MethodID,
            parameters: List<ComponentValue>,
            fn: CallCommandHandle.() -> Unit = {}
    ) = call(component, method, *parameters.toIntArray(), fn=fn)
}
