import java.net.InetAddress
import java.net.Socket

fun main(args: Array<String>) {
    val port = if (args.size == 2) args[1].toInt() else if (args.size == 1) args[0].toInt() else 1234
    val addr = if (args.size == 2) args[0] else "localhost"
    val adapter = AdapterClient(Socket(InetAddress.getByName(addr), port))

    adapter.disconnected.connect {
        println("Disconnected")
    }

    adapter.commandSubmitted.connect(::println)
}
