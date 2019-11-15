
/** Create an RCSComponentInfoSet given a set of component configurations. */
fun Set<RCSComponentConfiguration>.toInfoSet()
        = RCSComponentInfoSet(toList().map(::RCSComponentInfo).toSet())

/** Get the info object for a robot configuration, given a set of components. */
fun RCSRobotConfiguration.toInfo(componentSet: RCSComponentInfoSet)
        = RCSRobotInfo(this, componentSet)
