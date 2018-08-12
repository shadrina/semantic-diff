package ru.nsu.diff.engine.matching

import ru.nsu.diff.util.DeltaTreeElement

interface Matcher {
    fun DeltaTreeElement.label() = this@label.type
    fun DeltaTreeElement.value() = this@value.text
}