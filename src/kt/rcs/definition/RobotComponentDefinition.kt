package definition

typealias ComponentType = String
typealias ComponentAttribute = String
typealias ComponentValue = Int
typealias MethodName = String
typealias MethodParameterName = String

data class RobotComponentDefinition(
        val type: ComponentType,
        val headerPath: String,
        val sourcePath: String,
        val methods: List<DefinitionMethod>
)

data class DefinitionMethod(
        val name: MethodName,
        val returns: Boolean,
        val parameters: List<MethodParameter>
)

data class MethodParameter(
        val name: MethodParameterName,
        val defaultValue: ComponentValue?
)
