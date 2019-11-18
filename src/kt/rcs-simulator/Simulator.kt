import observables.Signal
import observables.UnitSignal
import rcs.Degrees
import rcs.Millimetres
import rcs.adapter.*
import rcs.cm
import rcs.deg

class Simulator(
        val robots: List<SimulatedRobot>,
        private val timeScale: Float = 1f,
        millimetresPerSecond: Millimetres = 30.cm,
        degreesPerSecond: Degrees = 60.deg
) {
    private var running = true

    init {
        Thread {
            while (running) {
                val request = synchronized(requests) {
                    if (requests.isNotEmpty()) requests.removeAt(0)
                    else null
                }

                if (request == null) {
                    Thread.sleep(100)
                    continue
                }

                when (request) {
                    is WaitRequest -> {
                        Thread.sleep((request.delay / timeScale).toLong())
                        request.complete()
                    }
                    is ForwardRequest -> {
                        var distance = request.distance

                        while (true) {
                            if (distance <= distancePerUpdate) {
                                moveForward(distance)
                                break
                            }
                            else {
                                moveForward(distancePerUpdate)
                                distance -= distancePerUpdate
                                Thread.sleep(MILLISECONDS_PER_UPDATE_UNSCALED)
                            }
                        }

                        request.complete()
                    }
                    is TurnRequest -> {
                        var angle = request.angle

                        while (true) {
                            if (angle <= anglePerUpdate) {
                                turn(angle)
                                break
                            }
                            else {
                                turn(anglePerUpdate)
                                angle -= anglePerUpdate
                                Thread.sleep(MILLISECONDS_PER_UPDATE_UNSCALED)
                            }
                        }

                        request.complete()
                    }
                    is CallRequest -> {
                        println("${request.component}.${request.parameters}(" +
                                "${request.parameters.joinToString()})")
                        request.complete(0)
                    }
                }
            }
        } .start()
    }

    fun attachAdapter(port: Int) {
        val server = AdapterServer(port)

        server.connected { client ->
            client.waitRequested.connect(this::addRequest)
            client.forwardRequested.connect(this::addRequest)
            client.turnRequested.connect(this::addRequest)
            client.callRequested.connect(this::addRequest)

            client.position.value(robots[0].position)

            client.position.changed.connect { position ->
                synchronized(robots) {
                    robots.forEach { it.position = position }
                }
            }
        }

        this.server = server
    }

    fun attachView(close: UnitSignal, draw: Signal<EnvironmentRenderer>) = draw.connect {
        synchronized(robots) { it.draw(robots) }
        close.connect(this::stop)
    }

    fun stop() {
        running = false
        server?.stop()
    }

    private fun addRequest(request: ActionRequest) {
        synchronized(requests) { requests.add(request) }
    }

    private fun moveForward(distance: Millimetres) {
        synchronized(robots) {
            robots.forEach { it.forward(distance) }
        }
    }

    private fun turn(angle: Degrees) {
        synchronized(robots) {
            robots.forEach { it.turn(angle) }
        }
    }

    private val UPDATES_PER_SECOND_UNSCALED = 20
    private val MILLISECONDS_PER_UPDATE_UNSCALED
            = (1000f / UPDATES_PER_SECOND_UNSCALED).toLong()
    private var server: AdapterServer? = null

    private val distancePerUpdate
            = millimetresPerSecond * timeScale / UPDATES_PER_SECOND_UNSCALED
    private val anglePerUpdate
            = degreesPerSecond * timeScale / UPDATES_PER_SECOND_UNSCALED

    private val requests: MutableList<ActionRequest> = mutableListOf()
}


