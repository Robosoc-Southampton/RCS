package cpp

import ComponentDefinition
import jsonDecodeComponentDefinition
import readFile
import readJSONFile

/** Definition of a component type along with its source code. */
data class ComponentSourceDefinition(
        /** Component definition. */
        val component: ComponentDefinition,
        /** Header source for the component. Should declare the type for the
         *  component and must declare a setup() method accepting the
         *  component'getS attributes as parameters. */
        val header: String,
        /** Source code for the component. Header is automatically included. */
        val source: String
)

//////////////////////////////////////////////////////////////////////////////////////////

/** Load a component source definition from a folder.
 *  The folder should contain a config.json, header.h, and source.cpp file. */
fun loadComponentSourceDefinition(folderPath: String): ComponentSourceDefinition {
    val config = readJSONFile("$folderPath/config.json", jsonDecodeComponentDefinition)
    val header = readFile("$folderPath/header.h")
    val source = readFile("$folderPath/source.cpp")

    return ComponentSourceDefinition(config, header, source)
}
