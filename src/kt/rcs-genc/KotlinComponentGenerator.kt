import rcs.definition.RCSComponentInfo

class KotlinComponentGenerator(component: RCSComponentInfo)
    : AbstractComponentGenerator(component)
{
    override val fileExtension = "kt"

    override fun getClassContent() = COMPONENT_TEMPLATE
            .replace("%NAME", componentCodeName)
            .replace("%METHODS", getMethods().joinToString("\n\n\t"))

    private fun getMethods() = component.methods.map { method ->
        "fun ${method.name}(${method.parameters.joinToString { "$it: ComponentValue" }})" +
                (if (method.returns) ": ComponentValue" else "") +
                " {\n\t\t" + (if (method.returns) "return " else "") +
                "controller.call(componentName, componentIndex, " +
                "\"${method.name}\", ${method.methodIndex}, " +
                "listOf(${method.parameters.joinToString()}))" +
                (if (method.returns) "!!" else "") + "\n\t}"
    }
}

private const val COMPONENT_TEMPLATE = """package component
        
import rcs.adapter.AbstractRobotController
import rcs.ComponentValue

class %NAME(
    private val controller: AbstractRobotController,
    private val componentName: String,
    private val componentIndex: Int
) {
    %METHODS
}
"""
