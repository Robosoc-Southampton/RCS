
class RCSRobotInfo(
        val configuration: RCSRobotConfiguration,
        val componentSet: RCSComponentInfoSet
) {
    val name = configuration.name

    val components by lazy {
        configuration.components.mapIndexed { idx, definition ->
            RCSRobotComponentInfo(idx, definition, componentSet)
        }
    }

    fun lookupComponent(name: String)
            = components.firstOrNull { it.name == name }
}

class RCSRobotComponentInfo(
        val componentIndex: Int,
        val definition: RCSRobotComponent,
        components: RCSComponentInfoSet
) {
    val name = definition.name
    val attributes = definition.attributes
    val tags = definition.tags.toSet()
    val component = components.lookup(definition.type) ?:
        throw RCSConfigurationError("No such component type '${definition.type}'")
}
