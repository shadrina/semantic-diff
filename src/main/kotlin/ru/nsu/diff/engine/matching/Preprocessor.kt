package ru.nsu.diff.engine.matching

import com.intellij.psi.tree.IElementType
import ru.nsu.diff.util.BinaryRelation
import ru.nsu.diff.util.DeltaTreeElement

class Preprocessor(private val relation: BinaryRelation<DeltaTreeElement>) : Matcher {
    fun match(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        val labels = mutableListOf<IElementType>()
        children1
                .filter { it.id !== null }
                .forEach { if (!labels.contains(it.label())) labels.add(it.label()) }

        for (label in labels) {
            val ids = mutableListOf<String>()

            val withLabel1 = children1.filter { it.label() == label }
            val withLabel2 = children2.filter { it.label() == label }
            withLabel1.forEach {
                if (it.id !== null && !ids.contains(it.id!!)) ids.add(it.id!!)
            }

            for (id in ids) {
                val fromT1 = withLabel1.filter { it.id == id }
                val fromT2 = withLabel2.filter { it.id == id }
                if (fromT1.size == 1 && fromT2.size == 1) {
                    val identified1 = fromT1.first()
                    val identified2 = fromT2.first()
                    relation.add(Pair(identified1, identified2))

                    // Match all their direct children
                    identified1.children.forEach { childFromT1 ->
                        val partner = identified2.children.find {
                            childFromT2 -> childFromT1.label() == childFromT2.label()
                        }
                        if (partner !== null) relation.add(Pair(childFromT1, partner))
                    }

                }
            }
        }
    }
}