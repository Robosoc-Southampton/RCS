import rcs.definition.RCSComponentInfo

class PythonComponentGenerator(component: RCSComponentInfo)
    : AbstractComponentGenerator(component)
{
    override val fileExtension = "py"

    override fun getClassContent(): String {
        TODO("not implemented")
    }
}
