package definition

import java.nio.file.Files
import java.nio.file.Paths

fun <T> parseFile(file: String, fn: (String, String) -> T): T {
    val content = String(Files.readAllBytes(Paths.get(file)))
    return fn(file, content)
}

fun parseComponentDefinition(
        file: String,
        content: String
): List<RobotComponentDefinition> = content.treeify().map { (_, type, cdata) ->
    val attrs = cdata.firstOrNull { it.line == "attributes" } ?.children ?: listOf()
    val meths = cdata.firstOrNull { it.line == "methods" } ?.children ?: listOf()
    val invalid = cdata.firstOrNull { it.line != "attributes" && it.line != "methods" }
    val base = file.split(Regex("[/\\\\]")).dropLast(1).joinToString("/")

    if (invalid != null) {
        throw ParseError("Invalid section '${invalid.line}' on line ${invalid.number} of '$file'")
    }

    val attributes = attrs.map { (_, a, _) ->
        val parts = a.split("=").map { it.trim() }
        DefinitionAttribute(parts[0], parts.getOrNull(1)?.toInt())
    }

    val methods = meths.map { (l, m, _) ->
        val parts0 = m.split("(")

        if (parts0.size != 2) {
            throw ParseError("Malformed method $m on line $l of '$file'")
        }

        val (name, restUntrimmed) = parts0
        val restTrimmed = if (restUntrimmed.isEmpty() || restUntrimmed.last() != ')')
            throw ParseError("Malformed method '$name' on line $l of '$file' (expected ')')")
        else restUntrimmed.dropLast(1)
        val params = restTrimmed.split(",").map { it.trim() }

        DefinitionMethod(name, params)
    }

    RobotComponentDefinition(
            type,
            "$base/header.h", "$base/source.cpp",
            methods, attributes)
}

fun parseRobotProfile(
        file: String,
        content: String
): List<RobotProfile> = content.treeify().map { (_, d, children) ->
    val name = d
    val components = children.map { (l, d, cdata) ->
        val parts = d.split(":").map { it.trim() }
        val attributes = cdata.map { (l, a, _) ->
            val attrParts = a.split("=").map { it.trim() }

            if (attrParts.size != 2) {
                throw ParseError("Expected '<name> = <value>' on line $l of '$file'")
            }

            InstanceAttribute(attrParts[0], attrParts[1].toInt())
        }

        if (parts.size != 2) {
            throw ParseError("Expected '<name>: <type>' on line $l of '$file'")
        }

        val (name, typeAndTagsJoined) = parts
        val typeAndTags = typeAndTagsJoined.split("#").map { it.trim() }

        RobotComponentInstance(name, typeAndTags[0], typeAndTags.drop(1), attributes)
    }

    RobotProfile(name, components)
}

data class ParseError(override val message: String): Throwable()

private typealias NestedLines = List<NestedLine>
private data class NestedLine(val number: Int, val line: String, val children: NestedLines)

private fun String.treeify(): NestedLines = lines()
        .map { it.replace("    ", "\t") }
        .mapIndexed { i, s -> s.takeWhile { it == '\t' } .length to (i to s.trim()) }
        .filter { it.second.second != "" }
        .treeify()

private fun List<Pair<Int, Pair<Int, String>>>.treeify(): NestedLines {
    if (isEmpty()) return listOf()
    val (i, line) = first()
    val children = drop(1).takeWhile { it.first > i }
    val rest = drop(1 + children.size)

    return listOf(NestedLine(line.first, line.second, children.treeify())) + rest.treeify()
}
