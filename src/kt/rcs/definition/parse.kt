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
): List<RobotComponentDefinition> = content.treeify().map { (_, type, meths) ->
    val base = file.split(Regex("[/\\\\]")).dropLast(1).joinToString("/")

    val methods = meths.map { (ln, m, _) ->
        val parts0 = m.split("(")

        if (parts0.size != 2) {
            throw ParseError("Malformed method $m on line $ln of '$file'")
        }

        val (name, restUntrimmed) = parts0
        val restTrimmed = if (restUntrimmed.isEmpty() || restUntrimmed.last() != ')')
            throw ParseError("Malformed method '$name' on line $ln of '$file' (expected ')')")
        else restUntrimmed.dropLast(1)
        val params = restTrimmed
                .takeIf { it != "" }
                ?.split(",")
                ?.map { p -> p.split("=").map { it.trim() } }
                ?.map { l -> MethodParameter(l[0], l.getOrNull(1)?.toInt()) }
                ?: listOf()

        DefinitionMethod(name.removePrefix("*"), name.startsWith("*"), params)
    }

    RobotComponentDefinition(
            type, "$base/header.h", "$base/source.cpp", methods)
}

fun parseRobotProfile(
        file: String,
        content: String
): List<RobotProfile> = content.treeify().map { (_, d, children) ->
    val name = d
    val components = children.map { (l, d, cdata) ->
        val parts = d.split(":").map { it.trim() }

        if (parts.size != 2) {
            throw ParseError("Expected '<name>: <type>' on line $l of '$file'")
        }

        val (name, typeAndTagsJoined) = parts
        val typeAndTags = typeAndTagsJoined.split("#").map { it.trim() }

        RobotComponentInstance(name, typeAndTags[0], typeAndTags.drop(1))
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
