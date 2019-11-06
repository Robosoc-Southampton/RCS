
/** An extended control handle exposes additional movement functionality on top
 *  of an existing control handle. */
abstract class ExtendedControlHandle(
        val handle: ControlHandle
): ControlHandle() {
    /** Face towards a position on the board. */
    abstract fun face(position: Location)

    /** Face in a given direction. */
    abstract fun face(direction: Direction)

    /** Go to a position on the board. */
    abstract fun goto(position: Location)
}