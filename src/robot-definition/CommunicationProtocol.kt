import java.io.InputStream

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

//////////////////////////////////////////////////////////////////////////////////////////

fun encodePacket(
        opcode: PacketOpcode,
        data: PacketDataBuilder.() -> Unit
): ByteArray {
    val builder = PacketDataBuilder()
    builder.integer(opcode)
    data(builder)
    return encodeInteger(builder.length()) + builder.data()
}

fun decodePacket(stream: InputStream, fn: (PacketOpcode, PacketDataDecoder) -> Unit) {
    if (stream.available() < 2) {
        return
    }

    val b0 = stream.read().toByte()
    val b1 = stream.read().toByte()
    val length = decodeInteger(b0, b1)
    val bytes = stream.readNBytes(length)
    val decoder = PacketDataDecoder(bytes.toMutableList())
    val opcode = decoder.integer()

    fn(opcode, decoder)
}

//////////////////////////////////////////////////////////////////////////////////////////

class PacketDataBuilder {
    private val data: MutableList<Byte> = mutableListOf()

    fun integer(data: Int) {
        this.data.addAll(encodeInteger(data).toList())
    }

    fun string(data: String) {
        this.data.addAll(data.toByteArray().toList())
    }

    fun length() = data.size
    fun data() = data.toByteArray()
}

class PacketDataDecoder(
        private var bytes: MutableList<Byte>
) {
    val length get() = bytes.size

    fun integer(): Int {
        val b0 = bytes.removeAt(0)
        val b1 = bytes.removeAt(0)

        return decodeInteger(b0, b1)
    }

    fun string(length: Int = this.length): String {
        return String(bytes.take(length).toByteArray())
                .also { bytes = bytes.drop(length).toMutableList() }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

private fun encodeInteger(integer: Int)
        = byteArrayOf((integer shr 8).toByte(), integer.toByte())

private fun decodeInteger(b0: Byte, b1: Byte)
        = b0.toInt() shl 8 + b1.toInt()
