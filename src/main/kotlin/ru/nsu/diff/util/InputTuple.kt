package ru.nsu.diff.util

data class InputTuple(
        val T1: DeltaTreeElement,
        val T2: DeltaTreeElement,
        val binaryRelation: BinaryRelation<DeltaTreeElement>
)