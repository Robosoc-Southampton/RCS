package rcs.adapter

import TCPConnectionServer

// TODO: docstrings

class AdapterServer(port: Int) {
    private val connected: MutableList<(AdapterServerClient) -> Unit> = mutableListOf()
    private val server = TCPConnectionServer(port)

    val running get() = server.running

    fun connected(fn: (AdapterServerClient) -> Unit) {
        synchronized(connected) { connected.add(fn) }
    }

    fun stop() {
        server.stop()
    }

    init {
        server.connected {
            val client = AdapterServerClient(it)
            synchronized(connected) { connected.forEach { it(client) } }
        }
    }

    companion object {
        const val DEFAULT_PORT = 1234
    }
}
