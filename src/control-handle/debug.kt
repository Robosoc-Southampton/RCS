
object ControlHandleDebugger {
    val waitLogger: WaitCommandHandle.() -> Unit = {
        started.connect { Logging.logf(t, "Wait started") }
        erred.connect { Logging.logf(t, "Wait errored %r", it) }
        completed.connect { Logging.logf(t, "Wait completed") }
    }

    val forwardLogger: ForwardCommandHandle.() -> Unit = {
        started.connect { Logging.logf(t, "Forward started") }
        status.connect { Logging.logf(t, "Moved forward to %b", it) }
        paused.connect { Logging.logf(t, "Forward paused at %b", it) }
        erred.connect { Logging.logf(t, "Forward errored %r", it) }
        aborted.connect { Logging.logf(t, "Forward aborted") }
        completed.connect { Logging.logf(t, "Forward completed") }
    }

    val turnLogger: TurnCommandHandle.() -> Unit = {
        started.connect { Logging.logf(t, "Turn started") }
        status.connect { Logging.logf(t, "Turned to %b", it) }
        erred.connect { Logging.logf(t, "Turn errored %r", it) }
        completed.connect { Logging.logf(t, "Turn completed") }
    }

    val callLogger: CallCommandHandle.() -> Unit = {
        started.connect { Logging.logf(t, "Call started") }
        erred.connect { Logging.logf(t, "Call errored %r", it) }
        finished.connect { Logging.logf(t, "Call finished (%b)", it) }
        completed.connect { Logging.logf(t, "Call completed") }
    }

    private const val t = "ControlHandleSignal"
}
