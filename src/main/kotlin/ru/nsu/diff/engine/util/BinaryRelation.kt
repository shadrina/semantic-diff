package ru.nsu.diff.engine.util

class BinaryRelation<T> {
    private val pairs: MutableList<Pair<T, T>> = mutableListOf()

    fun add(x: T, y: T) = pairs.add(Pair(x, y))
    fun getPartner(x: T) : T? {
        var result: T? = null
        pairs.forEach {
            if (it.first === x) result = it.second
            if (it.second === x) result = it.first
        }
        return result
    }
    fun containsElementWithPairOf(x: T) : Boolean = pairs.any { it.first === x || it.second === x }
    fun containsElement(pair: Pair<T, T>) = pairs.contains(pair)
}