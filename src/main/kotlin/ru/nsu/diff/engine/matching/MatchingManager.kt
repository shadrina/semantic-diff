package ru.nsu.diff.engine.matching

import ru.nsu.diff.engine.lang.LangCfg
import ru.nsu.diff.util.BinaryRelation
import ru.nsu.diff.util.DeltaTreeElement

class MatchingManager(
        private val langCfg: LangCfg,
        private val relation: BinaryRelation<DeltaTreeElement>
) {

    fun match(root1: DeltaTreeElement, root2: DeltaTreeElement) {
        val nodes1 = root1.nodes()
        val nodes2 = root2.nodes()
        Preprocessor(langCfg, relation).match(nodes1, nodes2)
        FastMatcher(langCfg, relation).match(nodes1, nodes2)
        // TODO: If relation contains one of roots, we need to create dummy roots for both trees and add them to relation
        if (!relation.containsPairFor(root1) && !relation.containsPairFor(root2)) relation.add(root1, root2)

        Postprocessor(langCfg, relation, root1.height()).match(root1)
    }
}