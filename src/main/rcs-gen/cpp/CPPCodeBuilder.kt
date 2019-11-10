package cpp

class CPPCodeBuilder {
    private val includes: MutableList<String> = mutableListOf()
    private val globals: MutableList<String> = mutableListOf()
    private val functions: MutableList<String> = mutableListOf()
    private val setup: MutableList<String> = mutableListOf()
    private val loop: MutableList<String> = mutableListOf()
    private val isrs: MutableList<String> = mutableListOf()

    init {
        setup {
            statement("TCCR2B = (TCCR2B & 0xF8) | 0x06")
            statement("TIMSK2 = (TIMSK2 & 0xF9) | 0x02")
            statement("OCR2A = 255")
        }
    }

    fun includes(file: String) {
        includes.add("#include \"${file}.h\"")
    }

    fun includesNative(file: String) {
        includes.add("#include <${file}.h>")
    }

    fun global(name: String, type: String, value: String? = null) {
        globals.add("$type $name${value?.let {" = $it"} ?: ""};")
    }

    fun function(name: String, returns: String, vararg parameters: Pair<String, String>,
                 fn: CPPBlockBuilder.() -> Unit) {
        val generator = CPPBlockBuilder()
        fn(generator)
        functions.add("$returns $name(${parameters.joinToString { (t, n) -> "$t $n" }}) {" +
                "\n\t${generator.toString().replace("\n", "\n\t")}\n}")
    }

    fun setup(fn: CPPBlockBuilder.() -> Unit) {
        val generator = CPPBlockBuilder()
        fn(generator)
        setup.add(generator.toString())
    }

    fun loop(fn: CPPBlockBuilder.() -> Unit) {
        val generator = CPPBlockBuilder()
        fn(generator)
        loop.add(generator.toString())
    }

    fun isr(fn: CPPBlockBuilder.() -> Unit) {
        val generator = CPPBlockBuilder()
        fn(generator)
        isrs.add(generator.toString())
    }

    override fun toString()
            = "${includes.joinToString("\n")}\n\n" +
              "${globals.joinToString("\n")}\n\n" +
              "${functions.joinToString("\n\n")}\n\n" +
              "void setup() {\n\t${setup.joinToString("\n\n").replace("\n", "\n\t")}\n}\n\n" +
              "void loop() {\n\t${loop.joinToString("\n\n").replace("\n", "\n\t")}\n}\n\n" +
              "ISR(TIMER2_COMPA_vect) {\n\t${isrs.joinToString("\n\n").replace("\n", "\n\t")}\n}"
}

class CPPBlockBuilder {
    private val output = StringBuilder()

    fun writeLine(s: String) {
        output.append("$s\n")
    }

    fun statement(s: String) {
        output.append("$s;\n")
    }

    fun block(s: String, fn: CPPBlockBuilder.() -> Unit) {
        val generator = CPPBlockBuilder()
        fn(generator)
        output.append(s)
        output.append(" {\n\t${generator
                .toString()
                .replace("\n", "\n\t")}\n}")
    }

    override fun toString() = output.toString().trim()
}
