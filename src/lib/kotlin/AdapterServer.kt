import observables.Signal
import observables.UnitSignal
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class AdapterServer(port: Int) {
    private val socket = ServerSocket(port)
    private val connections: MutableList<AdapterClient> = mutableListOf()
    val connected = Signal<AdapterClient>()

    fun start() {
        Thread {
            while (true) {
                val client = AdapterClient(socket.accept())

                client.disconnected.connect {
                    synchronized(connections) {
                        connections.remove(client)
                    }
                }

                synchronized(connections) {
                    connections.add(client)
                }

                connected.emit(client)
            }
        } .start()
    }

    fun broadcast(command: Command) {
        synchronized(connections) {
            connections.forEach {
                it.send(command)
            }
        }
    }
}

class AdapterClient(
        private val socket: Socket
) {
    val identifier = socket.inetAddress.canonicalHostName!! + ":" + socket.port
    val disconnected = UnitSignal()
    val commandSubmitted = Signal<Command>()

    fun send(command: Command) {
        socket.getOutputStream().write(command.encode().toByteArray())
        socket.getOutputStream().write("\n;\n".toByteArray())
        socket.getOutputStream().flush()
    }

    fun disconnect() {
        disconnected.emit()
    }

    init {
        val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()!!))

        disconnected.connect {
            synchronized(socket) { if (!socket.isClosed) socket.close() }
        }

        Thread {
            while (true) {
                val lines = mutableListOf<String>()

                try {
                    while (true) {
                        val line = inputStream.readLine() ?: throw SocketException()

                        if (line == ";") break

                        lines.add(line)
                    }
                }
                catch (e: SocketException) {
                    disconnect()
                    break
                }

                val packet = lines.joinToString("\n")

                try {
                    commandSubmitted.emit(jsonDecode(packet, Command.decoder))
                }
                catch (e: JSONDecodeError) {
                    e.printStackTrace()
                }
            }
        } .start()
    }
}
