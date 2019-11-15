import java.nio.file.Files
import java.nio.file.Paths

/** Exposes core functionality of RCS. */
object rcscore {

    /** Load a component definition from a folder. */
    fun loadComponentDefinition(path: String): RCSComponentDefinition {
        val config = loadComponentConfiguration("$path/config.json")
        val header = String(Files.readAllBytes(Paths.get(path, "header.h")))
        val source = String(Files.readAllBytes(Paths.get(path, "source.cpp")))
        return RCSComponentDefinition(config, header, source)
    }

    /** Load a component configuration from a file. */
    fun loadComponentConfiguration(path: String): RCSComponentConfiguration {
        return readJSONFile(path, jsonDecodeComponentDefinition)
    }

    /** Load a robot definition from a file. */
    fun loadRobotConfiguration(path: String): RCSRobotConfiguration {
        return readJSONFile(path, jsonDecodeRobotConfiguration)
    }

    //////////////////////////////////////////////////////////////////////////////////////

    /** Decode a component definition. */
    val jsonDecodeComponentDefinition = jsonDecodeObject {
        val name = "name" / jsonDecodeString
        val attributes = "attributes" / jsonDecodeArray(jsonDecodeComponentAttributeDefinition)
        val methods = "methods" / jsonDecodeArray(jsonDecodeComponentMethodDefinition)

        RCSComponentConfiguration(name, attributes, methods)
    }

    /** Decode a component attribute definition. */
    private val jsonDecodeComponentAttributeDefinition = jsonDecodeObject {
        val name = "name" / jsonDecodeString
        val defaultValue = decodeOptionalEntry("default", jsonDecodeOptional(jsonDecodeComponentValue))

        RCSComponentAttributeDefinition(name, defaultValue)
    }

    /** Decode a component method definition. */
    private val jsonDecodeComponentMethodDefinition = jsonDecodeObject {
        val name = "name" / jsonDecodeString
        val parameters = "parameters" / jsonDecodeArray(jsonDecodeString)
        val returns = "returns" / jsonDecodeBoolean

        RCSComponentMethodDefinition(name, parameters, returns)
    }

    /** Decode a robot configuration. */
    val jsonDecodeRobotConfiguration = jsonDecodeObject {
        val name = "name" / jsonDecodeString
        val components = "components" / jsonDecodeArray(jsonDecodeRobotComponent)

        RCSRobotConfiguration(name, components)
    }

    /** Decode an instantiation of a component type within a robot configuration. */
    private val jsonDecodeRobotComponent = jsonDecodeObject {
        val type = "type" / jsonDecodeString
        val name = "name" / jsonDecodeString
        val attributes = "attributes" / jsonDecodeArray(jsonDecodeRobotComponentAttribute)
        val tags = "tags" / jsonDecodeArray(jsonDecodeString)

        RCSRobotComponent(type, name, attributes, tags)
    }

    /** Decode a value binding to a robot's component's attribute. */
    private val jsonDecodeRobotComponentAttribute = jsonDecodeObject {
        val name = "name" / jsonDecodeString
        val value = "value" / jsonDecodeComponentValue

        RCSRobotComponentAttribute(name, value)
    }

}
