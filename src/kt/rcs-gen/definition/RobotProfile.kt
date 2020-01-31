package definition

typealias InstanceTag = String

data class RobotProfile(
        val name: String,
        val components: List<RobotComponentInstance>
)

data class RobotComponentInstance(
        val name: String,
        val type: ComponentType,
        val tags: List<InstanceTag>,
        val attributes: List<InstanceAttribute>
)

data class InstanceAttribute(
        val attribute: ComponentAttribute,
        val value: ComponentValue
)
