import rcs.definition.RCSComponentInfo
import java.nio.file.Files
import java.nio.file.Paths

abstract class AbstractComponentGenerator(
        val component: RCSComponentInfo
) {
    val componentCodeName = component.name

    abstract val fileExtension: String

    fun generate(path: String) {
        Files.write(Paths.get(path, "$componentCodeName.$fileExtension"),
                getClassContent().toByteArray())
    }

    abstract fun getClassContent(): String
}
