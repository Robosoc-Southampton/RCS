import rcs.definition.RCSRobotInfo

class PythonControllerGenerator(robot: RCSRobotInfo)
    : AbstractControllerGenerator(robot)
{
    override val fileExtension = "py"

    override fun getClassContent(): String {
        TODO("not implemented")
    }
}
