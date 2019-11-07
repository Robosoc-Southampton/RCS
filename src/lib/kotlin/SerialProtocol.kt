
typealias PacketOpcode = Int
typealias PacketID = Int

private const val FORWARD_BASE: PacketOpcode = 10
private const val TURN_BASE: PacketOpcode = 20
private const val CALL_BASE: PacketOpcode = 32

const val MAX_METHODS_PER_COMPONENT = 8

const val DEBUG_INFO_OPCODE: PacketOpcode = 1

const val FORWARD_INSTRUCTION: PacketOpcode = FORWARD_BASE
const val FORWARD_FEEDBACK_STARTED: PacketOpcode = FORWARD_BASE + 1
const val FORWARD_FEEDBACK_ERRED: PacketOpcode = FORWARD_BASE + 2
const val FORWARD_FEEDBACK_COMPLETED: PacketOpcode = FORWARD_BASE + 3
const val FORWARD_FEEDBACK_PAUSED: PacketOpcode = FORWARD_BASE + 4
const val FORWARD_FEEDBACK_ABORTED: PacketOpcode = FORWARD_BASE + 5
const val FORWARD_FEEDBACK_STATUS: PacketOpcode = FORWARD_BASE + 6

const val TURN_INSTRUCTION: PacketOpcode = TURN_BASE
const val TURN_FEEDBACK_STARTED: PacketOpcode = TURN_BASE + 1
const val TURN_FEEDBACK_ERRED: PacketOpcode = TURN_BASE + 2
const val TURN_FEEDBACK_COMPLETED: PacketOpcode = TURN_BASE + 3
const val TURN_FEEDBACK_STATUS: PacketOpcode = TURN_BASE + 4

const val CALL_INSTRUCTION: PacketOpcode = CALL_BASE
const val CALL_FEEDBACK_STARTED: PacketOpcode = CALL_BASE + 1
const val CALL_FEEDBACK_ERRED: PacketOpcode = CALL_BASE + 2
const val CALL_FEEDBACK_COMPLETED: PacketOpcode = CALL_BASE + 3
const val CALL_FEEDBACK_FINISHED: PacketOpcode = CALL_BASE + 4
