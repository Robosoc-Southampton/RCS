package kt

class KotlinCodeBuilder {
    private val imports: MutableList<String> = mutableListOf()
    private val classes: MutableList<KotlinClassBuilder> = mutableListOf()

    fun imports(file: String) {
        imports.add("import $file")
    }

    fun writeClass(name: String, superName: String? = null, fn: KotlinClassBuilder.() -> Unit) {
        val builder = KotlinClassBuilder(name, superName)
        fn(builder)
        classes.add(builder)
    }

    override fun toString()
            = "${imports.joinToString("\n")}\n\n" +
              classes.joinToString("\n\n")
}

class KotlinClassBuilder(
        private val name: String,
        private val superName: String?
) {
    private val constructorParameters: MutableList<String> = mutableListOf()
    private val fields: MutableList<String> = mutableListOf()
    private val methods: MutableList<String> = mutableListOf()

    fun parameter(name: String, type: String, pub: Boolean = false) {
        constructorParameters.add(("private ".takeIf { !pub } ?: "") + "val $name: $type")
    }

    fun field(name: String, value: String, pub: Boolean = false) {
        fields.add(("private ".takeIf { !pub } ?: "") + "val $name = $value")
    }

    fun mutableField(name: String, value: String) {
        fields.add("private var $name = $value")
    }

    fun method(name: String, returns: String, vararg parameters: Pair<String, String>,
               fn: KotlinBlockBuilder.() -> Unit) {
        val generator = KotlinBlockBuilder()
        fn(generator)
        methods.add("fun $name(${parameters.joinToString { (n, t) -> "$n: $t" }}): $returns {" +
                "\n\t${generator.toString().replace("\n", "\n\t")}\n}")
    }

    fun overrideMethod(name: String, returns: String, vararg parameters: Pair<String, String>,
               fn: KotlinBlockBuilder.() -> Unit) {
        val generator = KotlinBlockBuilder()
        fn(generator)
        methods.add("override fun $name(${parameters.joinToString { (n, t) -> "$n: $t" }}): $returns {" +
                "\n\t${generator.toString().replace("\n", "\n\t")}\n}")
    }

    override fun toString(): String {
        val paramsString = constructorParameters.joinToString(", ")
        val fieldsString = fields.joinToString("\n\t")
        val methodsString = methods.joinToString("\n\n").replace("\n", "\n\t")

        return "class $name($paramsString)${superName?.prependIndent(": ") ?: ""} {\n" +
                "\t" + fieldsString + "\n\n" +
                "\t" + methodsString + "\n" +
                "}"
    }
}

class KotlinBlockBuilder {
    private val output = StringBuilder()

    fun writeLine(s: String) {
        output.append("$s\n")
    }

    fun block(s: String, fn: KotlinBlockBuilder.() -> Unit) {
        val generator = KotlinBlockBuilder()
        fn(generator)
        output.append(s)
        output.append(" {\n\t${generator
                .toString()
                .replace("\n", "\n\t")}\n}\n\n")
    }

    override fun toString() = output.toString().trim()
}
