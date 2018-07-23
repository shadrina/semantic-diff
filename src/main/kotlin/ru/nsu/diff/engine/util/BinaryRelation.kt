package ru.nsu.diff.engine.util

class BinaryRelation<T> {
    private val pairs: MutableList<Pair<T, T>> = mutableListOf()

    fun add(x: T, y: T) {
        pairs.add(Pair(x, y))
    }

    fun hasPair(x: T) : Boolean {
        return pairs.any {
            it.first === x || it.second === x
        }
    }
}