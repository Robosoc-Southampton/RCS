import rcs.util.ArgumentParser
import rcs.util.writeFile
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

val parser = ArgumentParser.create("rcs-robot-config") {
    flag("interactive", "-i", "Run in interactive (GUI) mode")
    switch("name", "-n", 1,
            optional = true,
            description = "Name of the robot to generate")
}

fun parseArguments(args: Array<String>) = parser.parse(args).let {
    val configPath = it.values.getOrNull(0)
            ?: run { parser.printUsage(); exitProcess(1) }
    val robotName = it.optionalValue("name") ?: configPath
            .replace(Regex(".*/"), "")
            .replace(Regex("\\.json$"), "")

    Triple(configPath, robotName, it.flag("interactive"))
}

class MyView: View() {
    override val root = vbox {
        label("TODO")
    }
}

class MyApp: App(MyView::class) {

}

fun main(args: Array<String>) {
    val (configPath, robotName, interactive) = parseArguments(args)
    val exists = Files.exists(Paths.get(configPath))

    when {
        interactive -> {
            if (!exists) createTemplate(robotName, configPath)
            launch<MyApp>(args)
        }
        exists -> println("File already exists")
        else -> createTemplate(robotName, configPath)
    }
}

private fun createTemplate(name: String, path: String) {
    writeFile(path, CONFIG_TEMPLATE
            .replace("%NAME", name))
}

private const val CONFIG_TEMPLATE = """
{
  "name": "%NAME",
  "components": [
    {
      "type": "LED",
      "name": "LED_BUILTIN",
      "attributes": [
        { "name": "pin", "value": 13 }
      ],
      "tags": []
    }
  ]
}
"""
