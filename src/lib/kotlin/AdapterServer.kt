import observables.Signal
import java.net.ServerSocket

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
