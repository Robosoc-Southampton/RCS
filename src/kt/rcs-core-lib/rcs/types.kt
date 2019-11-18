package rcs

import JSONDecoder
import JSONEncoder
import jsonDecodeInteger
import jsonDecodeNumber
import jsonDecodeObject
import jsonEncodeInteger
import jsonEncodeNumber
import jsonEncodeObject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

typealias Milliseconds = Float
typealias Millimetres = Float
typealias Degrees = Float

/** A value given to components as attributes or method parameters. */
typealias ComponentValue = Int

/** A 2D position vector. */
data class PositionVec2D(val x: Millimetres = 0.mm, val y: Millimetres = x)
/** A 2D direction vector. */
data class DirectionVec2D(val dx: Millimetres = 0.mm, val dy: Millimetres = dx)
/** A 2D position and direction. */
data class RobotPosition(val location: PositionVec2D, val direction: DirectionVec2D = DirectionVec2D(0.mm, 1.mm))

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

operator fun PositionVec2D.plus(direction: DirectionVec2D)
        = PositionVec2D(x + direction.dx, y + direction.dy)

operator fun PositionVec2D.minus(location: PositionVec2D)
        = DirectionVec2D(x - location.x, y - location.y)

operator fun DirectionVec2D.times(scale: Float)
        = DirectionVec2D(dx * scale, dy * scale)

val DirectionVec2D.length: Millimetres
    get() = sqrt(dx * dx + dy * dy)

val DirectionVec2D.angle: Degrees
    get() = atan2(dy, dx) * 180 / Math.PI.toFloat()

fun DirectionVec2D.normalise()
        = this * (1 / length)

fun DirectionVec2D.rotate(theta: Degrees): DirectionVec2D {
    if (theta == 90.deg)
        return DirectionVec2D(-dy, dx)

    if (theta == (-90).deg)
        return DirectionVec2D(dy, -dx)

    val sinT = sin(theta / 180 * Math.PI)
    val cosT = cos(theta / 180 * Math.PI)

    return DirectionVec2D(
            (dx * cosT - dy * sinT).toFloat(),
            (dx * sinT + dy * cosT).toFloat()
    )
}

fun RobotPosition.forward(distance: Millimetres)
        = RobotPosition(location + direction * distance, direction)

fun RobotPosition.rotate(angle: Degrees)
        = RobotPosition(location, direction.rotate(angle))

//////////////////////////////////////////////////////////////////////////////////////////

val jsonEncodeMilliseconds: JSONEncoder<Milliseconds> = jsonEncodeNumber
val jsonEncodeMillimetres: JSONEncoder<Millimetres> = jsonEncodeNumber
val jsonEncodeDegrees: JSONEncoder<Degrees> = jsonEncodeNumber
val jsonEncodeComponentValue: JSONEncoder<ComponentValue> = jsonEncodeInteger

val jsonDecodeMilliseconds: JSONDecoder<Milliseconds> = jsonDecodeNumber
val jsonDecodeMillimetres: JSONDecoder<Millimetres> = jsonDecodeNumber
val jsonDecodeDegrees: JSONDecoder<Degrees> = jsonDecodeNumber
val jsonDecodeComponentValue: JSONDecoder<ComponentValue> = jsonDecodeInteger

val jsonEncodePositionVec2D: JSONEncoder<PositionVec2D> = jsonEncodeObject { location ->
    "x" - location.x / jsonEncodeMillimetres
    "y" - location.y / jsonEncodeMillimetres
}

val jsonEncodeDirectionVec2D: JSONEncoder<DirectionVec2D> = jsonEncodeObject { direction ->
    "dx" - direction.dx / jsonEncodeMillimetres
    "dy" - direction.dy / jsonEncodeMillimetres
}

val jsonEncodeRobotPosition: JSONEncoder<RobotPosition> = jsonEncodeObject { position ->
    "location" - position.location / jsonEncodePositionVec2D
    "direction" - position.direction / jsonEncodeDirectionVec2D
}

val jsonDecodePositionVec2D: JSONDecoder<PositionVec2D> = jsonDecodeObject {
    val x = "x" / jsonDecodeMillimetres
    val y = "y" / jsonDecodeMillimetres
    PositionVec2D(x, y)
}

val jsonDecodeDirectionVec2D: JSONDecoder<DirectionVec2D> = jsonDecodeObject {
    val dx = "dx" / jsonDecodeMillimetres
    val dy = "dy" / jsonDecodeMillimetres
    DirectionVec2D(dx, dy)
}

val jsonDecodeRobotPosition: JSONDecoder<RobotPosition> = jsonDecodeObject {
    val location = "location" / jsonDecodePositionVec2D
    val direction = "direction" / jsonDecodeDirectionVec2D
    RobotPosition(location, direction)
}
