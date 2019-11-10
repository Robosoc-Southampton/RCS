package cpp

import AdapterServer
import Command
import observables.Signal

class StaticRoutineReader(port: Int) {
    val submitted = Signal<List<Command>>()

    init {
        val adapter = AdapterServer(port)

        adapter.connected.connect { client ->
            synchronized(open) {
                if (open) {
                    println("Receiving static routine from " + client.identifier)
                    client.commandSubmitted.connect { commands.add(it) }
                    client.disconnected.connect { submitted.emit(commands) }
                    open = false
                }
            }
        }

        adapter.start()
    }

    private var open = true
    private val commands: MutableList<Command> = mutableListOf()
}
