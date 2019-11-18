import rcs.util.ArgumentParser

// rcs-simulator -r r1.json r2.json...
fun main(args: Array<String>) {
    val parser = ArgumentParser.create("rcs-simulator") {
        switch("robot configuration", "-r", null,
                optional = true,
                description = "Configuration files for robots to add initially")
    }

    val arguments = parser.parse(args)
}
