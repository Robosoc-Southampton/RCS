import rcs.definition.RCSRobotInfo

class KotlinControllerGenerator(robot: RCSRobotInfo)
    : AbstractControllerGenerator(robot)
{
    override val fileExtension = "kt"

    override fun getClassContent(): String {
        val imports = mutableListOf<String>()
        val components = mutableListOf<String>()

        robot.components.forEach {
            imports.add("import component." + it.component.name)
            components.add("val ${it.name} = ${it.component.name}(this, \"${it.name}\", ${it.componentIndex})")
        }

        return CONTROLLER_TEMPLATE
                .replace("%IMPORTS", imports.joinToString("\n"))
                .replace("%NAME", robotCodeName)
                .replace("%COMPONENTS", components.joinToString("\n\t"))
    }
}

private const val CONTROLLER_TEMPLATE = """import rcs.adapter.AbstractRobotController
%IMPORTS

class %NAME(host: String, port: Int): AbstractRobotController(host, port) {
    %COMPONENTS
}
"""
