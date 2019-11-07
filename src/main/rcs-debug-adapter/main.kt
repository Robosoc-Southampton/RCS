
fun main(args: Array<String>) {
    val adapter = AdapterServer(args.getOrElse(0) { "1234" } .toInt())

    adapter.start()

    adapter.connected.connect { client ->
        println("Client ${client.identifier} connected")

        client.disconnected.connect {
            println("Client ${client.identifier} disconnected")
        }

        client.commandSubmitted.connect { command ->
            println("$command")
        }
    }
}
