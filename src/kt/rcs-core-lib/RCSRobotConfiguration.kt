
/** Definition of a robot's component set. */
data class RCSRobotConfiguration(
        /** Name of the robot. */
        val name: String,
        /** List of components on the robot. */
        val components: List<RCSRobotComponent>
)

////////////////////////////////////////////////////////////////////////////////

/** Specification of a component on a robot. */
data class RCSRobotComponent(
        /** Name of the component type of this component. */
        val type: String,
        /** Identifier for this component. */
        val name: String,
        /** List of attributes of this component. Should match the component
         *  type definition's attributes. */
        val attributes: List<RCSRobotComponentAttribute>,
        /** Meta information e.g. "primary-motor-controller". */
        val tags: List<String>
)

////////////////////////////////////////////////////////////////////////////////

/** Binding of a value to an attribute */
data class RCSRobotComponentAttribute(
        /** Name of the attribute of the component. */
        val name: String,
        /** Value of the binding. */
        val value: ComponentValue
)
