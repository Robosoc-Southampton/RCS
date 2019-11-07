import observables.Signal
import observables.UnitSignal

class ControlError(override val message: String): Exception(message)

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * A command handle represents the status of a submitted command.
 *
 * Various signals will be emitted, some specific to the type of the control
 * command.
 */
sealed class CommandHandle {
    /** Signal emitted when the control is started. */
    val started = UnitSignal()

    /** Signal emitted when the control is completed. */
    val completed = UnitSignal()

    /** Signal emitted when an error is encountered. */
    val erred = Signal<ControlError>()
}

//////////////////////////////////////////////////////////////////////////////////////////

class WaitCommandHandle: CommandHandle()

class ForwardCommandHandle: CommandHandle() {
    /** Signal emitted when the movement is paused, with the current distance
     *  the robot has travelled as a parameter. */
    val paused = Signal<Millimetres>()

    /** Signal emitted when the movement is aborted with a reason. */
    val aborted = Signal<String>()

    /** Signal providing status updates on the movement. */
    val status = Signal<Millimetres>()
}

class TurnCommandHandle: CommandHandle() {
    /** Signal providing status updates on the movement. */
    val status = Signal<Degrees>()
}

class CallCommandHandle: CommandHandle() {
    /** Signal emitted when the component method call finishes, with the value
     *  returned if applicable. */
    val finished = Signal<ComponentValue?>()
}
