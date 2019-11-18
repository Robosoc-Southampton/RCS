import rcs.definition.RCSRobotInfo
import rcs.util.writeFile

abstract class AbstractControllerGenerator(
        val robot: RCSRobotInfo
) {
    val robotCodeName = robot.name.toCamelCase()

    abstract val fileExtension: String

    fun generate(path: String) {
        writeFile("$path/$robotCodeName.$fileExtension", getClassContent())
    }

    abstract fun getClassContent(): String
}

fun String.toCamelCase() = (substring(0, 1).toUpperCase() + substring(1))
        .replace(Regex("-(.)")) { it.groupValues[1].toUpperCase() }
