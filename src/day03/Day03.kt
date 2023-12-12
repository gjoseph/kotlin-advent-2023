package day03

import multiply
import printResult
import readDayInput
import readTestInput

data class CharAndPos(val char: Char, val pos: Int)

fun main() {

    data class Schematic(val columnWith: Int, val allChars: CharArray)

    fun parse(input: List<String>): Schematic {
        val columnWidth = input.map { it.length }.toSet().single()
        val allChars: CharArray = input.flatMap { it.toList() }.toCharArray()
        return Schematic(columnWidth, allChars)
    }

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

    fun findAdjacentNumbers(c: CharAndPos, schematic: Schematic): List<Int> {
        val chars = schematic.allChars
        return adjacentPositions(c.pos, schematic.columnWith, chars.size)
            .map { potentialNrPos ->
                // i am starting to regret not using a 2d array but oh well, i'll follow through
                if (chars[potentialNrPos].isDigit()) {
                    var numStart = potentialNrPos
                    while (numStart > 0 && chars[numStart - 1].isDigit()) {
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
                while (chars[i].isDigit()) {
                    nums += chars[i]
                    i++
                }
                Integer.valueOf(nums.concatToString())
            }

    }

    fun part1(input: List<String>): Int {
        val s: Schematic = parse(input)
        return s.allChars.mapIndexed { i, c -> CharAndPos(c, i) }
            // find symbols ... assuming anything non-digit and not-a-dot _is_ a symbol
            .filterNot { it.char.isDigit() || it.char == '.' }
            // find numbers adjacent to each symbol
            .flatMap { findAdjacentNumbers(it, s) }
            // sum'em up
            .sum()
    }

    fun part2(input: List<String>): Int {
        val s: Schematic = parse(input)
        return s.allChars.mapIndexed { i, c -> CharAndPos(c, i) }
            // find gears
            .filter {  it.char == '*' }
            .map { findAdjacentNumbers(it, s) }
            // valid gears have exactly 2 part numbers near them
            .filter { it.size == 2 }
            // multiply the 2 values -- "gear ratio"
            .map(List<Int>::multiply)
            // sum'em up
            .sum()
    }

    // tests
    // i should really write more tests to test clamping
    check(adjacentPositions(7, 5, 20) == listOf(1, 2, 3, 6, 8, 11, 12, 13))
    check(adjacentPositions(4, 5, 20) == listOf(3, 8, 9))
    check(adjacentPositions(15, 5, 20) == listOf(10, 11, 16))
    check(part1(readTestInput(1)) == 4361)
    check(part2(readTestInput(1)) == 467835)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}
