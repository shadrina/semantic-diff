package ru.nsu.diff.engine.transforming

import com.intellij.psi.PsiElement

import ru.nsu.diff.engine.transforming.EditOperationType.*
import ru.nsu.diff.engine.util.InputTuple
import ru.nsu.diff.engine.util.Queue

object EditScriptGenerator {
    private lateinit var inputTuple: InputTuple
    private val script = EditScript()

    fun generateScript(inputTuple: InputTuple) : EditScript {
        this.inputTuple = inputTuple

        val queue = Queue<PsiElement>()
        queue.enqueue(inputTuple.T2)
        bfs(queue, mutableListOf())

        return script
    }

    private fun bfs(queue: Queue<PsiElement>, visited: MutableList<PsiElement>) {
        val curr = queue.dequeue() ?: return
        visited.add(curr)
        curr.children.forEach { queue.enqueue(it) }

        val currParent = curr.parent
        val partner = inputTuple.binaryRelation.getPartner(curr)

        // Insert-phase
        if (partner == null && currParent != null) {
            val newNode = curr.copy()
            val dstNode = inputTuple.binaryRelation.getPartner(currParent)
            val insertOperation = EditOperation(INSERT, newNode, dstNode, curr.findPosition())
            insertOperation.perform()
        }
        // Move-phase
        if (partner != null && currParent != null) {
            val partnerParent = partner.parent
            val moveOperation = EditOperation(MOVE, curr, partnerParent, curr.findPosition())
            moveOperation.perform()
        }
        // Align-phase
        curr.alignChildren()
    }

    private fun PsiElement.findPosition() : Int = this.parent.children.indexOf(this)
    private fun PsiElement.alignChildren() {

    }
}