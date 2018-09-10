open class Base(val name: String) {

    init { println("Initializing Base") }

    open val size: Int =
            name.length.also { println("Initializing size in Base: $it") }

    /**
     * INSERTED COMMENT
     */
}

class Derived(
        val lastName: String,
        name: String
) : Base(name.capitalize().also { println("Argument for Base: $it") }) {

    init { println("Initializing Derived") }

    fun moved() {}

    override val size: Int =
            (super.size + lastName.length).also { println("Initializing size in Derived: $it") }

    class FutureNested {
        // smth
    }
}

class CycleWrapping {
    fun countSmth(smth: Int) {
        while (true) {
            uselessCalculating(smth++)
        }
    }
}