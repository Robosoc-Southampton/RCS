import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

typealias Milliseconds = Float
typealias Millimetres = Float
typealias Degrees = Float

typealias ComponentID = String
typealias MethodID = String
/** A value given to components as attributes or method parameters. */
typealias ComponentValue = Int

data class Location(val x: Millimetres = 0.mm, val y: Millimetres = x)
data class Direction(val dx: Millimetres = 0.mm, val dy: Millimetres = dx)
data class RobotPosition(val location: Location, val direction: Direction = Direction(0.mm, 1.mm))

//////////////////////////////////////////////////////////////////////////////////////////

val Float.ms: Milliseconds get() = this
val Float.s: Milliseconds get() = (this * 1000).ms
val Int.ms: Milliseconds get() = this.toFloat().ms
val Int.s: Milliseconds get() = (this * 1000).ms

val Float.mm: Millimetres get() = this
val Float.cm: Millimetres get() = (this * 10).mm
val Float.m: Millimetres get() = (this * 100).cm
val Int.mm: Millimetres get() = this.toFloat().mm
val Int.cm: Millimetres get() = (this * 10).mm
val Int.m: Millimetres get() = (this * 100).cm

val Float.deg: Degrees get() = this
val Float.rad: Degrees get() = this * 180 / Math.PI.toFloat()
val Int.deg: Degrees get() = this.toFloat().deg
val Int.rad: Degrees get() = this * 180 / Math.PI.toFloat()

//////////////////////////////////////////////////////////////////////////////////////////

operator fun Location.plus(direction: Direction)
        = Location(x + direction.dx, y + direction.dy)

operator fun Location.minus(location: Location)
        = Direction(x - location.x, y - location.y)

operator fun Direction.times(scale: Millimetres)
        = Direction(dx * scale, dy * scale)

val Direction.length: Millimetres
    get() = sqrt(dx * dx + dy * dy)

val Direction.angle: Degrees
    get() = atan2(dy, dx) * 180 / Math.PI.toFloat()

fun Direction.normalise()
        = this * (1 / length)

fun Direction.rotate(theta: Degrees): Direction {
    if (theta == 90.deg)
        return Direction(-dy, dx)

    if (theta == (-90).deg)
        return Direction(dy, -dx)

    val sinT = sin(theta / 180 * Math.PI)
    val cosT = cos(theta / 180 * Math.PI)

    return Direction((dx * cosT - dy * sinT).toFloat(), (dx * sinT + dy * cosT).toFloat())
}

fun RobotPosition.forward(distance: Millimetres)
        = RobotPosition(location + direction * distance, direction)

fun RobotPosition.rotate(angle: Degrees)
        = RobotPosition(location, direction.rotate(angle))

//////////////////////////////////////////////////////////////////////////////////////////

val jsonEncodeMilliseconds: JSONEncoder<Milliseconds> = jsonEncodeNumber
val jsonEncodeMillimetres: JSONEncoder<Millimetres> = jsonEncodeNumber
val jsonEncodeDegrees: JSONEncoder<Degrees> = jsonEncodeNumber
val jsonEncodeComponentID: JSONEncoder<ComponentID> = jsonEncodeString
val jsonEncodeMethodID: JSONEncoder<MethodID> = jsonEncodeString
val jsonEncodeComponentValue: JSONEncoder<ComponentValue> = jsonEncodeInteger

val jsonDecodeMilliseconds: JSONDecoder<Milliseconds> = jsonDecodeNumber
val jsonDecodeMillimetres: JSONDecoder<Millimetres> = jsonDecodeNumber
val jsonDecodeDegrees: JSONDecoder<Degrees> = jsonDecodeNumber
val jsonDecodeComponentID: JSONDecoder<ComponentID> = jsonDecodeString
val jsonDecodeMethodID: JSONDecoder<MethodID> = jsonDecodeString
val jsonDecodeComponentValue: JSONDecoder<ComponentValue> = jsonDecodeInteger

val jsonEncodeLocation: JSONEncoder<Location> = jsonEncodeObject { location ->
    "x" - location.x / jsonEncodeMillimetres
    "y" - location.y / jsonEncodeMillimetres
}

val jsonEncodeDirection: JSONEncoder<Direction> = jsonEncodeObject { direction ->
    "dx" - direction.dx / jsonEncodeMillimetres
    "dy" - direction.dy / jsonEncodeMillimetres
}

val jsonEncodeRobotPosition: JSONEncoder<RobotPosition> = jsonEncodeObject { position ->
    "location" - position.location / jsonEncodeLocation
    "direction" - position.direction / jsonEncodeDirection
}

val jsonDecodeLocation: JSONDecoder<Location> = jsonDecodeObject {
    val x = "x" / jsonDecodeMillimetres
    val y = "y" / jsonDecodeMillimetres
    Location(x, y)
}

val jsonDecodeDirection: JSONDecoder<Direction> = jsonDecodeObject {
    val dx = "dx" / jsonDecodeMillimetres
    val dy = "dy" / jsonDecodeMillimetres
    Direction(dx, dy)
}

val jsonDecodeRobotPosition: JSONDecoder<RobotPosition> = jsonDecodeObject {
    val location = "location" / jsonDecodeLocation
    val direction = "direction" / jsonDecodeDirection
    RobotPosition(location, direction)
}
