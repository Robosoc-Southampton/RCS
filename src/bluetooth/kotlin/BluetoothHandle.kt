import java.lang.RuntimeException

/** A wrapper around a bluetooth connection. */
class BluetoothHandle(
        mac: String,
        program: String = "src/bluetooth/python/bluetooth-console.py"
) {
    /** Send data across the bluetooth connection. */
    fun send(data: ByteArray) {
        process.outputStream.write(data)
        process.outputStream.flush()
    }

    /** Close the bluetooth connection. */
    fun close() {
        process.outputStream.close()
        Thread.sleep(50)
        process.destroy()
    }

    private val process = Runtime.getRuntime().exec(arrayOf(
            "python",
            program,
            "--addr",
            mac
    ))

    init {
        process.inputStream.read()

        process.onExit().whenComplete { _, _ ->
            if (process.exitValue() != 0) {
                throw RuntimeException(String(process.errorStream.readAllBytes()))
            }
        }
    }
}
