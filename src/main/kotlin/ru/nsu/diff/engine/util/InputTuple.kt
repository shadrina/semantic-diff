package ru.nsu.diff.engine.util

data class InputTuple(
        val T1: DeltaTreeElement,
        val T2: DeltaTreeElement,
        val binaryRelation: BinaryRelation<DeltaTreeElement>
)