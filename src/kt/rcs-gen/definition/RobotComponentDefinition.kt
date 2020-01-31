package definition

typealias ComponentType = String
typealias ComponentAttribute = String
typealias ComponentValue = Int
typealias MethodName = String
typealias AttributeName = String
typealias MethodParameterName = String

data class RobotComponentDefinition(
        val type: ComponentType,
        val headerPath: String,
        val sourcePath: String,
        val methods: List<DefinitionMethod>,
        val attributes: List<DefinitionAttribute>
)

data class DefinitionMethod(
        val name: MethodName,
        val parameters: List<MethodParameterName>
)

data class DefinitionAttribute(
        val name: AttributeName,
        val defaultValue: ComponentValue?
)
