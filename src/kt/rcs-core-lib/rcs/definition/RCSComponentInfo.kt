package rcs.definition

/** Info for a set of components. */
data class RCSComponentInfoSet(
        /** All components included. */
        val components: Set<RCSComponentInfo>
) {
    /** Lookup a component type given its type name. */
    fun lookup(name: String)
            = components.firstOrNull { it.name == name }
}

////////////////////////////////////////////////////////////////////////////////

/** Info for a component type. */
data class RCSComponentInfo(
        /** The source configuration for the component type. */
        val configuration: RCSComponentConfiguration
) {
    /** Name of the component type. */
    val name = configuration.name

    /** Attributes of the component type. */
    val attributes by lazy {
        configuration.attributes.mapIndexed(::RCSComponentAttributeInfo)
    }

    /** Methods of the component type. */
    val methods by lazy {
        configuration.methods.mapIndexed(::RCSComponentMethodInfo)
    }

    /** Lookup an attribute of the component type, given its name. */
    fun lookupAttribute(name: String)
            = attributes.firstOrNull { it.name == name }

    /** Lookup a method of the component type, given its name. */
    fun lookupMethod(name: String)
            = methods.firstOrNull { it.name == name }
}

////////////////////////////////////////////////////////////////////////////////

/** Info for a method of a component type. */
data class RCSComponentMethodInfo internal constructor(
        /** Index of the method within the set of methods available. */
        val methodIndex: Int,
        /** Source definition of the method. */
        val definition: RCSComponentMethodDefinition
) {
    /** Name of the method. */
    val name = definition.name
    /** Parameter names of the method. */
    val parameters = definition.parameters
    /** Whether the method returns (true if so). */
    val returns = definition.returns
}

////////////////////////////////////////////////////////////////////////////////

/** Info for an attribute of a component type. */
data class RCSComponentAttributeInfo internal constructor(
        /** Index of the attribute within the set of attributes available. */
        val attributeIndex: Int,
        /** Source definition of the attribute. */
        val definition: RCSComponentAttributeDefinition
) {
    /** Name of the attribute. */
    val name = definition.name
    /** Default value of the attribute. */
    val defaultValue = definition.defaultValue
}
