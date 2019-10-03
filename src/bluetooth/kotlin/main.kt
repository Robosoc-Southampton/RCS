
fun main() {
    val handle = BluetoothHandle("98:D3:32:11:17:5F")

    handle.send(byteArrayOf(128.toByte()))
    Thread.sleep(1000)
    handle.send(byteArrayOf(0.toByte()))
    Thread.sleep(1000)
    handle.close()
}
