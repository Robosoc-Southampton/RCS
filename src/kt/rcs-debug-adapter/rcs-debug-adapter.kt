import rcs.adapter.AdapterServer
import rcs.util.ArgumentParser

fun main(args: Array<String>) {
    val parser = ArgumentParser.create("rcs-debug-adapter") {
        switch("port", "-p", 1,
                optional = true,
                description = "Port to host adapter on")
    }
    val arguments = parser.parse(args)
    val server = AdapterServer(arguments.optionalValue("port")?.toInt()
            ?: AdapterServer.DEFAULT_PORT)

    server.connected { client ->
        println("Client connected")

        client.waitRequested.connect {
            println(it)
            it.complete()
        }

        client.forwardRequested.connect {
            println(it)
            it.complete()
        }

        client.turnRequested.connect {
            println(it)
            it.complete()
        }

        client.callRequested.connect {
            println(it)
            it.complete(null)
        }

        client.position.connect {
            println("Position is now $it")
        }
    }
}
