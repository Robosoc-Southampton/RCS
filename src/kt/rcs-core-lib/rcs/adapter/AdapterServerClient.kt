package rcs.adapter

import JSONDecodeError
import TCPConnectionClient
import TCPMessage
import jsonDecodeArray
import jsonDecodeInteger
import jsonDecodeObject
import jsonDecodeString
import jsonEncodeObject
import jsonParse
import observables.Observable
import observables.Signal
import rcs.*

// TODO: docstrings

class AdapterServerClient internal constructor(
        private val client: TCPConnectionClient
) {
    val position = Observable(RobotPosition(PositionVec2D()))
    val waitRequested = Signal<WaitRequest>()
    val forwardRequested = Signal<ForwardRequest>()
    val turnRequested = Signal<TurnRequest>()
    val callRequested = Signal<CallRequest>()

    init {
        client.received { message ->
            val json = try { jsonParse(message.content) }
            catch (e: JSONDecodeError) { return@received }

            when (jsonDecodeObject(json) { "command" / jsonDecodeString }) {
                POSITION_UPDATE -> {
                    val pos = jsonDecodeObject(json) { "position" / jsonDecodeRobotPosition }
                    position.value(pos)
                    message.send("ok")
                }
                WAIT_COMMAND -> {
                    val value = jsonDecodeObject(json) { "value" / jsonDecodeMilliseconds }
                    waitRequested.emit(WaitRequest(message, value))
                }
                FORWARD_COMMAND -> {
                    val value = jsonDecodeObject(json) { "value" / jsonDecodeMillimetres }
                    forwardRequested.emit(ForwardRequest(message, value))
                }
                TURN_COMMAND -> {
                    val value = jsonDecodeObject(json) { "value" / jsonDecodeDegrees }
                    turnRequested.emit(TurnRequest(message, value))
                }
                CALL_COMMAND -> {
                    val component = jsonDecodeObject(json) { "component" / jsonDecodeString }
                    val componentIndex = jsonDecodeObject(json) { "componentIndex" / jsonDecodeInteger }
                    val method = jsonDecodeObject(json) { "method" / jsonDecodeString }
                    val methodIndex = jsonDecodeObject(json) { "methodIndex" / jsonDecodeInteger }
                    val parameters = jsonDecodeObject(json) { "parameters" / jsonDecodeArray(jsonDecodeComponentValue) }
                    callRequested.emit(CallRequest(message, component, componentIndex, method, methodIndex, parameters))
                }
            }
        }

        Thread.sleep(100)

        position.connect {
            client.send(jsonEncodeObject {
                "command" - POSITION_UPDATE
                "position" - it / jsonEncodeRobotPosition
            })
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

sealed class ActionRequest(
        private val message: TCPMessage
) {
    private var completed = false

    protected fun complete(content: String) {
        if (!completed) {
            completed = true
            message.send(content)
        }
    }
}

class WaitRequest(
        message: TCPMessage,
        val delay: Milliseconds
): ActionRequest(message) {
    fun complete() { complete("ok") }

    operator fun component0() = delay
    override fun toString() = "WaitRequest($delay)"
}

class ForwardRequest(
        message: TCPMessage,
        val distance: Millimetres
): ActionRequest(message) {
    fun complete() { complete("ok") }

    operator fun component0() = distance
    override fun toString() = "ForwardRequest($distance)"
}

class TurnRequest(
        message: TCPMessage,
        val angle: Degrees
): ActionRequest(message) {
    fun complete() { complete("ok") }

    operator fun component0() = angle
    override fun toString() = "TurnRequest($angle)"
}

class CallRequest(
        message: TCPMessage,
        val component: String,
        val componentIndex: Int,
        val method: String,
        val methodIndex: Int,
        val parameters: List<ComponentValue>
): ActionRequest(message) {
    fun complete(result: ComponentValue?) {
        complete(result?.toString() ?: CALL_RESULT_NO_VALUE)
    }

    operator fun component0() = component
    operator fun component1() = componentIndex
    operator fun component2() = method
    operator fun component3() = methodIndex
    operator fun component4() = parameters
    override fun toString() = "CallRequest($component, $componentIndex, $method, $methodIndex, $parameters)"
}
