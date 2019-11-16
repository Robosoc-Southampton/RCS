import rcs.definition.RCSRobotInfo
import java.nio.file.Files
import java.nio.file.Paths

abstract class AbstractControllerGenerator(
        val robot: RCSRobotInfo
) {
    val robotCodeName = robot.name.toCamelCase()

    abstract val fileExtension: String

    fun generate(path: String) {
        Files.write(Paths.get(path, "$robotCodeName.$fileExtension"),
                getClassContent().toByteArray())
    }

    abstract fun getClassContent(): String
}

fun String.toCamelCase() = (substring(0, 1).toUpperCase() + substring(1))
        .replace(Regex("-(.)")) { it.groupValues[1].toUpperCase() }
