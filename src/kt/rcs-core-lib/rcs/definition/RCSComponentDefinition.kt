package rcs.definition

import rcs.definition.RCSComponentConfiguration

/** Definition of a component. */
data class RCSComponentDefinition(
        /** The component'rcs.getS configuration. */
        val configuration: RCSComponentConfiguration,
        /** The content of the component'rcs.getS C++ header file. */
        val header: String,
        /** The content of the component'rcs.getS C++ source file. */
        val source: String
)
