
/** Definition of a component. */
data class RCSComponentDefinition(
        /** The component's configuration. */
        val configuration: RCSComponentConfiguration,
        /** The content of the component's C++ header file. */
        val header: String,
        /** The content of the component's C++ source file. */
        val source: String
)
