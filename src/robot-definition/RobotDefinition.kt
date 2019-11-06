
/** Definition of a robot's component set. */
data class RobotDefinition(
        /** Name of the robot. */
        val name: String,
        /** List of components on the robot. */
        val components: List<RobotComponent>
)

/** Specification of a component on a robot. */
data class RobotComponent(
        /** Name of the component type of this component. */
        val type: String,
        /** Identifier for this component. */
        val name: String,
        /** List of attributes of this component. Should match the component
         *  type definition's attributes. */
        val attributes: List<RobotComponentAttribute>,
        /** Meta information e.g. "primary-motor-controller". */
        val tags: List<String>
)

/** Binding of a value to an attribute */
data class RobotComponentAttribute(
        /** Name of the attribute of the component. */
        val name: String,
        /** Value of the binding. */
        val value: ComponentValue
)

//////////////////////////////////////////////////////////////////////////////////////////

/** Load a robot definition from a file. */
fun loadRobotDefinition(path: String): RobotDefinition {
    return readJSONFile(path, jsonDecodeRobotDefinition)
}

//////////////////////////////////////////////////////////////////////////////////////////

val jsonDecodeRobotDefinition = jsonDecodeObject {
    val name = "name" / jsonDecodeString
    val components = "components" / jsonDecodeArray(jsonDecodeRobotComponent)

    RobotDefinition(name, components)
}

val jsonDecodeRobotComponent = jsonDecodeObject {
    val type = "type" / jsonDecodeString
    val name = "name" / jsonDecodeString
    val attributes = "attributes" / jsonDecodeArray(jsonDecodeRobotComponentAttribute)
    val tags = "tags" / jsonDecodeArray(jsonDecodeString)

    RobotComponent(type, name, attributes, tags)
}

val jsonDecodeRobotComponentAttribute = jsonDecodeObject {
    val name = "name" / jsonDecodeString
    val value = "value" / jsonDecodeComponentValue

    RobotComponentAttribute(name, value)
}
