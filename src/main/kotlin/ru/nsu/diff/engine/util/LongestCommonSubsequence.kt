package ru.nsu.diff.engine.util

object LongestCommonSubsequence {
    fun <T> find(S1: List<T>, S2: List<T>, equal: (T, T) -> Boolean): List<Pair<T, T>> {
        val lcs = Array(S1.size + 1) { IntArray(S2.size + 1) }
        val solution = Array(S1.size + 1) { Array(S2.size + 1) { "" } }

        for (i in 0..S2.size) {
            lcs[0][i] = 0
            solution[0][i] = "0"
        }
        for (i in 0..S1.size) {
            lcs[i][0] = 0
            solution[i][0] = "0"
        }

        for (i in 1..S1.size)
            for (j in 1..S2.size)
                if (equal(S1[i - 1], S2[j - 1])) {
                    lcs[i][j] = lcs[i - 1][j - 1] + 1
                    solution[i][j] = "diagonal"
                } else {
                    lcs[i][j] = Math.max(lcs[i - 1][j], lcs[i][j - 1])
                    if (lcs[i][j] == lcs[i - 1][j]) {
                        solution[i][j] = "top"
                    } else {
                        solution[i][j] = "left"
                    }
                }

        var a = S1.size
        var b = S2.size
        var x = solution[a][b]
        val answer: MutableList<Pair<T, T>> = mutableListOf()
        while (x !== "0") {
            when {
                solution[a][b] === "diagonal" -> {
                    answer.add(Pair(S1[a - 1], S2[b - 1]))
                    a--
                    b--
                }
                solution[a][b] === "left" -> b--
                solution[a][b] === "top" -> a--
            }
            x = solution[a][b]
        }
        return answer.reversed()
    }
}