import rcs.definition.RCSComponentInfo
import rcs.util.writeFile

abstract class AbstractComponentGenerator(
        val component: RCSComponentInfo
) {
    val componentCodeName = component.name

    abstract val fileExtension: String

    fun generate(path: String) {
        writeFile("$path/$componentCodeName.$fileExtension", getClassContent())
    }

    abstract fun getClassContent(): String
}
