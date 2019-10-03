import java.lang.Exception

fun main() {
    val rt = Runtime.getRuntime()
    val command = arrayOf(
            "python",
            "src/bluetooth/python/bluetooth-console.py",
            "--addr",
            "98:D3:32:11:17:5F"
    )
    val proc = rt.exec(command)

    proc.inputStream.read()

    try {
        println("Here")

        proc.outputStream.write(byteArrayOf(128.toByte()))
        proc.outputStream.flush()

        Thread.sleep(1000)

        proc.outputStream.write(byteArrayOf(0.toByte()))
        proc.outputStream.flush()

        Thread.sleep(1000)

        proc.outputStream.close()
    }
    catch (e: Exception) {
        e.printStackTrace()
    }

    println("in")
    proc.destroy()
    println("out")
    println("Exit value: ${proc.exitValue()}")

    println(String(proc.inputStream.readAllBytes()))
    println(String(proc.errorStream.readAllBytes()))
}
