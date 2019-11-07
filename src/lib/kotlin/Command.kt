
sealed class Command {
    abstract fun encode(): String

    companion object {
        val decoder: JSONDecoder<Command> = { value -> jsonDecodeObject(value) {
            when (val c = "command" / jsonDecodeString) {
                ConnectCommand.command -> ConnectCommand.decoder(value)
                DisconnectCommand.command -> DisconnectCommand.decoder(value)
                InfoCommand.command -> InfoCommand.decoder(value)
                PositionCommand.command -> PositionCommand.decoder(value)
                WaitCommand.command -> WaitCommand.decoder(value)
                ForwardCommand.command -> ForwardCommand.decoder(value)
                TurnCommand.command -> TurnCommand.decoder(value)
                CallCommand.command -> CallCommand.decoder(value)
                else -> throw JSONDecodeError("Invalid 'command' attribute ('$c')")
            }
        } }
    }
}

object ConnectCommand: Command() {
    override fun encode() = jsonEncodeObject {
        "command" - command
    }

    const val command = "connect"

    val decoder = jsonDecodeObject { ConnectCommand }
}

object DisconnectCommand: Command() {
    override fun encode() = jsonEncodeObject {
        "command" - command
    }

    const val command = "disconnect"

    val decoder = jsonDecodeObject { DisconnectCommand }
}

data class InfoCommand(val info: String): Command() {
    override fun encode() = jsonEncodeObject {
        "command" - command
        "info" - info / jsonEncodeString
    }

    companion object {
        const val command = "info"

        val decoder = jsonDecodeObject {
            InfoCommand("info" / jsonDecodeString)
        }
    }
}

data class PositionCommand(val position: RobotPosition): Command() {
    override fun encode() = jsonEncodeObject {
        "command" - command
        "position" - position / jsonEncodeRobotPosition
    }

    companion object {
        const val command = "position"

        val decoder = jsonDecodeObject {
            PositionCommand("position" / jsonDecodeRobotPosition)
        }
    }
}

data class WaitCommand(val delay: Milliseconds): Command() {
    override fun encode() = jsonEncodeObject {
        "command" - command
        "delay"   - delay / jsonEncodeMilliseconds
    }

    companion object {
        const val command = "wait"

        val decoder = jsonDecodeObject {
            WaitCommand("delay" / jsonDecodeMilliseconds)
        }
    }
}

data class ForwardCommand(val distance: Millimetres): Command() {
    override fun encode() = jsonEncodeObject {
        "command"  - command
        "distance" - distance / jsonEncodeMillimetres
    }

    companion object {
        const val command = "forward"

        val decoder = jsonDecodeObject {
            ForwardCommand("distance" / jsonDecodeMillimetres)
        }
    }
}

data class TurnCommand(val angle: Degrees): Command() {
    override fun encode() = jsonEncodeObject {
        "command" - command
        "angle"   - angle / jsonEncodeDegrees
    }

    companion object {
        const val command = "turn"

        val decoder = jsonDecodeObject {
            TurnCommand("angle" / jsonDecodeDegrees)
        }
    }
}

data class CallCommand(
        val component: ComponentID,
        val componentIndex: Int,
        val method: MethodID,
        val methodIndex: Int,
        val parameters: List<ComponentValue>
): Command() {
    override fun encode() = jsonEncodeObject {
        "command"        - command
        "component"      - component / jsonEncodeComponentID
        "componentIndex" - componentIndex / jsonEncodeInteger
        "method"         - method / jsonEncodeMethodID
        "methodIndex"    - methodIndex / jsonEncodeInteger
        "parameters"     - parameters / jsonEncodeArray(jsonEncodeComponentValue)
    }

    companion object {
        const val command = "call"

        val decoder = jsonDecodeObject {
            CallCommand(
                    "component" / jsonDecodeComponentID,
                    "componentIndex" / jsonDecodeInteger,
                    "method" / jsonDecodeMethodID,
                    "methodIndex" / jsonDecodeInteger,
                    "parameters" / jsonDecodeArray(jsonDecodeComponentValue)
            )
        }
    }
}
