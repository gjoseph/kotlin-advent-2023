package day08

import kotlin.time.measureTimedValue
import printResult
import readDayInput
import readTestInput
import requireSize

enum class Direction { L, R }

fun main() {
    fun part1(input: List<String>): Int {
        val directions: List<Direction> = input.first().toCharArray().map { Direction.valueOf(it.toString()) }
        val map = input.drop(2).map { s ->
            val (key, left, right) = s.split(" = (", ", ", ")").filterNot { it.isEmpty() }.requireSize(3)
            Pair(key, Pair(left, right))
        }.toMap()
        var moves = 0
        var curr = "AAA"
        while (curr != "ZZZ") {
            val d = directions[moves % directions.size]
            curr = when (d) {
                Direction.L -> map.get(curr)!!.first
                Direction.R -> map.get(curr)!!.second
            }
            moves++
        }
        return moves
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // tests
    check(part1(readTestInput(1)) == 2)
    check(part1(readTestInput(2)) == 6)
    // check(part2(readTestInput(3)) == 6)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

