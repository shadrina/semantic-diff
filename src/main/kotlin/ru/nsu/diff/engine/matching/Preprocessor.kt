package ru.nsu.diff.engine.matching

import com.intellij.psi.tree.IElementType
import ru.nsu.diff.lang.LangCfg
import ru.nsu.diff.util.BinaryRelation
import ru.nsu.diff.util.DeltaTreeElement

class Preprocessor(
        private val langCfg: LangCfg,
        private val relation: BinaryRelation<DeltaTreeElement>
) : Matcher {
    fun match(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        matchUniqueInternals(children1, children2)

        val labels = children1.labelList()
        for (label in labels) {
            val withLabel1 = children1.filter { it.label() == label }
            val withLabel2 = children2.filter { it.label() == label }

            val ids = withLabel1.idList()
            for (id in ids) {
                val fromT1 = withLabel1.filter { it.id == id }
                val fromT2 = withLabel2.filter { it.id == id }
                if (fromT1.size == 1 && fromT2.size == 1) {
                    val identified1 = fromT1.first()
                    val identified2 = fromT2.first()
                    relation.add(Pair(identified1, identified2))

                    // Match all their direct children
                    identified1.children.forEach { childFromT1 ->
                        val partner = identified2.children.find { childFromT2 ->
                            childFromT1.label() == childFromT2.label()

                                // TODO: wrong, e.g. value arguments can be different, but we should match them here
                                && (childFromT1.value() == childFromT2.value() || !childFromT1.isLeaf() && !childFromT2.isLeaf())

                                    && !relation.containsPairFor(childFromT1)
                                && !relation.containsPairFor(childFromT2)
                        }
                        if (partner !== null) relation.add(Pair(childFromT1, partner))
                    }

                }
            }
        }
    }

    private fun matchUniqueInternals(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        langCfg.uniqueInternalsNames.forEach { name ->
            val fromT1 = children1.find { it.name == name }
            val fromT2 = children2.find { it.name == name }
            if (fromT1 !== null && fromT2 !== null) relation.add(fromT1, fromT2)
        }
    }

    private fun List<DeltaTreeElement>.labelList() : List<IElementType> {
        val labels = mutableListOf<IElementType>()
        this
                .filter { it.id !== null }
                .forEach { if (!labels.contains(it.label())) labels.add(it.label()) }
        return labels
    }

    private fun List<DeltaTreeElement>.idList() : List<String> {
        val ids = mutableListOf<String>()
        this
                .filter { it.id !== null && !ids.contains(it.id!!) }
                .forEach { ids.add(it.id!!) }
        return ids
    }
}