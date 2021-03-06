package rcs.adapter

import JSONDecodeError
import TCPMessage
import jsonDecodeObject
import jsonDecodeString
import jsonEncodeArray
import jsonEncodeInteger
import jsonEncodeObject
import jsonEncodeString
import jsonParse
import observables.Signal
import rcs.*

// TODO: docstrings

open class AbstractRobotController(
        host: String,
        port: Int
) {
    /** Signal emitted when the robot's position has changed. */
    val positionChanged = Signal<RobotPosition>()

    /** Robot's current position. */
    lateinit var position: RobotPosition
        private set

    /** Wait the specified time. */
    fun wait(delay: Milliseconds) {
        client.request(jsonEncodeObject {
            "command" - WAIT_COMMAND
            "value" - delay / jsonEncodeMilliseconds
        })
    }

    /** Move the robot forwards by a distance in millimetres. */
    fun forward(distance: Millimetres) {
        client.request(jsonEncodeObject {
            "command" - FORWARD_COMMAND
            "value" - distance / jsonEncodeMilliseconds
        })
    }

    /** Turn the robot by an angle in degrees. */
    fun turn(angle: Degrees) {
        client.request(jsonEncodeObject {
            "command" - TURN_COMMAND
            "value" - angle / jsonEncodeMilliseconds
        })
    }

    /** Call a method on a named component of the robot with parameters. */
    fun call(
            component: String, componentIndex: Int,
            method: String, methodIndex: Int,
            parameters: List<ComponentValue>
    ): ComponentValue? {
        val result = client.request(jsonEncodeObject {
            "command" - CALL_COMMAND
            "component" - component / jsonEncodeString
            "componentIndex" - componentIndex / jsonEncodeInteger
            "method" - method / jsonEncodeString
            "methodIndex" - methodIndex / jsonEncodeInteger
            "parameters" - parameters / jsonEncodeArray(jsonEncodeComponentValue)
        })

        return if (result.content == CALL_RESULT_NO_VALUE) null else result.content.toInt()
    }

    fun position(pos: RobotPosition) {
        client.request(jsonEncodeObject {
            "command" - POSITION_UPDATE
            "position" - pos / jsonEncodeRobotPosition
        })
    }

    fun disconnect() {
        client.disconnect()
    }

    ////////////////////////////////////////////////////////////////////////////

    private val client = TCPConnectionClient.connect(host, port)

    init {
        val message = client.receive()
        client.received(::decodeMessage)
        decodeMessage(message)
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun decodeMessage(message: TCPMessage) {
        val json = try { jsonParse(message.content) }
        catch (e: JSONDecodeError) { return }

        when (jsonDecodeObject(json) { "command" / jsonDecodeString }) {
            POSITION_UPDATE -> {
                val pos = jsonDecodeObject(json) { "position" / jsonDecodeRobotPosition }
                position = pos
                positionChanged.emit(pos)
            }
        }
    }
}
