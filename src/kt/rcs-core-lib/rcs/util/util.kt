package rcs.util

import rcs.definition.*
import java.nio.file.Files
import java.nio.file.Paths

/** Create an RCSComponentInfoSet given a set of component configurations. */
fun Set<RCSComponentConfiguration>.toInfoSet()
        = RCSComponentInfoSet(toList().map(::RCSComponentInfo).toSet())

/** Get the info object for a robot configuration, given a set of components. */
fun RCSRobotConfiguration.toInfo(componentSet: RCSComponentInfoSet)
        = RCSRobotInfo(this, componentSet)

/** Get the info object for a component configuration. */
fun RCSComponentConfiguration.toInfo()
        = RCSComponentInfo(this)

/** Write content to a file, creating required directories. */
fun writeFile(file: String, content: String) {
    val path = Paths.get(file)
    path.parent?.let { Files.createDirectories(it) }
    Files.write(path, content.toByteArray())
}
