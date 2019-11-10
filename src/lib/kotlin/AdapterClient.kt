import observables.Signal
import observables.UnitSignal
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.net.SocketException

class AdapterClient(
        private val socket: Socket
) {
    val identifier = socket.inetAddress.canonicalHostName!! + ":" + socket.port
    val disconnected = UnitSignal()
    val commandSubmitted = Signal<Command>()

    fun send(command: Command) {
        synchronized(socket) {
            if (socket.isClosed) return

            val os = socket.getOutputStream()

            try {
                os.write(command.encode().toByteArray())
                os.write("\n;\n".toByteArray())
                os.flush()
            }
            catch (e: SocketException) { /* do nothing, it'll be caught in the read loop */ }
        }
    }

    fun disconnect() {
        synchronized(socket) {
            if (!socket.isClosed) {
                socket.close()
                disconnected.emit()
            }
        }
    }

    init {
        val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()!!))

        Thread {
            while (true) {
                val lines = mutableListOf<String>()

                try {
                    while (true) {
                        val line = inputStream.readLine() ?: break
                        if (line == ";") break
                        lines.add(line)
                    }
                }
                catch (e: SocketException) {
                    disconnected.emit()
                    break
                }

                if (lines.isEmpty()) {
                    disconnect()
                    break
                }
                else {
                    val packet = lines.joinToString("\n")

                    try {
                        commandSubmitted.emit(jsonDecode(packet, Command.decoder))
                    }
                    catch (e: JSONDecodeError) {
                        e.printStackTrace()
                    }
                }
            }
        } .start()
    }
}
