package day03

import printResult
import readDayInput
import readTestInput

data class CharAndPos(val char: Char, val pos: Int)

fun main() {

    fun adjacentPositions(pos: Int, colWidth: Int, totalLength: Int): List<Int> {
        // there is likely a cleverer way of doing this
        val col = pos % colWidth
        fun lineOf(pos: Int): Int = (pos - col) / colWidth
        val line = lineOf(pos)
        fun clampToLine(pos: Int, line: Int): Int {
            val startOfLineIncl = (line) * colWidth
            val endOfLineExcl = (line + 1) * colWidth
            return if (pos in startOfLineIncl..<endOfLineExcl) pos else -1
        }

        fun pos(relativeLine: Int, relativeCol: Int): Int {
            val res = ((line + relativeLine) * colWidth) + (col + relativeCol)
            return clampToLine(res, (line + relativeLine))
        }

        return listOf(
            pos(-1, -1), pos(-1, 0), pos(-1, 1),
            pos(0, -1), pos(0, 1),
            pos(+1, -1), pos(+1, 0), pos(+1, 1),
        )
            .filter { it >= 0 && it < totalLength } // TODO it<totalLength should already be done by clamp() !?
    }

    fun findAdjacentNumbers(c: CharAndPos, allChars: CharArray, columnWidth: Int): List<Int> {
        return adjacentPositions(c.pos, columnWidth, allChars.size)
            .map { potentialNrPos ->
                // i am starting to regret not using a 2d array but oh well, i'll follow through
                if (allChars[potentialNrPos].isDigit()) {
                    var numStart = potentialNrPos
                    while (numStart > 0 && allChars[numStart - 1].isDigit()) {
                        numStart--
                    }
                    numStart
                } else {
                    -1
                }
            }
            .filter { it >= 0 }
            .toSet() // lazily remove duplicates -- some "adjacent" digits will pertain to the same number-start-position
            .map { numStart ->
                var nums = charArrayOf()
                var i = numStart
                while (allChars[i].isDigit()) {
                    nums += allChars[i]
                    i++
                }
                Integer.valueOf(nums.concatToString())
            }

    }

    fun part1(input: List<String>): Int {
        val columnWidth = input.map { it.length }.toSet().single()
        val allChars = input.flatMap { it.toList() }.toCharArray()
        // find symbols ... assuming anything non-digit _is_ a symbol
        return allChars.mapIndexed { i, c -> CharAndPos(c, i) }
            .filterNot { it.char.isDigit() || it.char == '.' }
            // find numbers adjacent to each symbol
            .flatMap { findAdjacentNumbers(it, allChars, columnWidth) }
            // sum'em up
            .sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // tests
    // i should really write more tests to test clamping
    check(adjacentPositions(7, 5, 20) == listOf(1, 2, 3, 6, 8, 11, 12, 13))
    check(adjacentPositions(4, 5, 20) == listOf(3, 8, 9))
    check(adjacentPositions(15, 5, 20) == listOf(10, 11, 16))
    check(part1(readTestInput(1)) == 4361)
    // check(part2(readTestInput(1)) == 467835)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}
