
/** Configuration of a component type, e.g. a servo. */
data class RCSComponentConfiguration(
        /** Name of the component */
        val name: ComponentID,
        /** Attributes of the component (i.e. parameters/configuration) */
        val attributes: List<RCSComponentAttributeDefinition>,
        /** Methods that may be called upon an instance of this component type. */
        val methods: List<RCSComponentMethodDefinition>
)

////////////////////////////////////////////////////////////////////////////////

/** Definition of an attribute of a component type */
data class RCSComponentAttributeDefinition(
        /** Name of the attribute. */
        val name: String,
        /** Optional default value for this attribute. */
        val defaultValue: ComponentValue?
)

////////////////////////////////////////////////////////////////////////////////

/** Definition of a method on a component type. */
data class RCSComponentMethodDefinition(
        /** Name of the method. */
        val name: MethodID,
        /** Names of the parameters to this method. */
        val parameters: List<String>,
        /** Whether the method returns any information. */
        val returns: Boolean
)
