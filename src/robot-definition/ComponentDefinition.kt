
/** A value given to components as attributes or method parameters. */
typealias ComponentValue = Int

/** Definition of a component type, e.g. a servo. */
data class ComponentDefinition(
        /** Name of the component */
        val name: String,
        /** Attributes of the component (i.e. parameters/configuration) */
        val attributes: List<ComponentAttributeDefinition>,
        /** Methods that may be called upon an instance of this component type. */
        val methods: List<ComponentMethodDefinition>
)

/** Definition of an attribute of a component type */
data class ComponentAttributeDefinition(
        /** Name of the attribute. */
        val name: String,
        /** Optional default value for this attribute. */
        val defaultValue: ComponentValue?
)

/** Definition of a method on a component type. */
data class ComponentMethodDefinition(
        /** Name of the method. */
        val name: String,
        /** Names of the parameters to this method. */
        val parameters: List<String>,
        /** Whether the method returns any information. */
        val returns: Boolean
)

//////////////////////////////////////////////////////////////////////////////////////////

val jsonDecodeComponentValue: JSONDecoder<ComponentValue> = jsonDecodeInteger

val jsonDecodeComponentDefinition = jsonDecodeObject {
    val name = "name" / jsonDecodeString
    val attributes = "attributes" / jsonDecodeArray(jsonDecodeComponentAttributeDefinition)
    val methods = "methods" / jsonDecodeArray(jsonDecodeComponentMethodDefinition)

    ComponentDefinition(name, attributes, methods)
}

val jsonDecodeComponentAttributeDefinition = jsonDecodeObject {
    val name = "name" / jsonDecodeString
    val defaultValue = "default" / jsonDecodeOptional(jsonDecodeComponentValue)

    ComponentAttributeDefinition(name, defaultValue)
}

val jsonDecodeComponentMethodDefinition = jsonDecodeObject {
    val name = "name" / jsonDecodeString
    val parameters = "parameters" / jsonDecodeArray(jsonDecodeString)
    val returns = "returns" / jsonDecodeBoolean

    ComponentMethodDefinition(name, parameters, returns)
}
