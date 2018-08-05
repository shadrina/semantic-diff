package ru.nsu.diff.engine.matching

import ru.nsu.diff.util.DeltaTreeElement

interface Matcher {
    fun match(root1: DeltaTreeElement, root2: DeltaTreeElement)
}