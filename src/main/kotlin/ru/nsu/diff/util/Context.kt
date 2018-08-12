package ru.nsu.diff.util

enum class ContextLevel {
    TOP_LEVEL,
    CLASS_MEMBER,
    LOCAL,
    EXPRESSION
}

data class ContextInfo(val contextLevel: ContextLevel, val contextProvider: DeltaTreeElement)