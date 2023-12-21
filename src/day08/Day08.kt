package day08

import kotlin.time.measureTimedValue
import printResult
import readDayInput
import readTestInput
import requireSize

enum class Direction { L, R }
data class Destinations(val left:String, val right:String)
data class Doc(val directions: List<Direction>,val map: Map<String, Destinations>)

fun main() {
    fun parse(input: List<String>): Doc {
        val directions: List<Direction> = input.first().toCharArray().map { Direction.valueOf(it.toString()) }
        val map: Map<String, Destinations> = input.drop(2).map { s ->
            val (key, left, right) = s.split(" = (", ", ", ")").filterNot { it.isEmpty() }.requireSize(3)
            Pair(key, Destinations(left, right))
        }.toMap()
        return Doc(directions, map)
    }

    fun part1(input: List<String>): Int {
        val (directions, map) = parse(input)
        var moves = 0
        var curr = "AAA"
        while (curr != "ZZZ") {
            val d = directions[moves % directions.size]
            curr = when (d) {
                Direction.L -> map.get(curr)!!.left
                Direction.R -> map.get(curr)!!.right
            }
            moves++
        }
        return moves
    }

    fun part2(input: List<String>): Int {
        val (directions, map) = parse(input)
        var moves = 0
        var curr = map.filterKeys { it.endsWith('A') }.keys
        var bestZCount = 0
        while (!curr.all { it.endsWith('Z') }) {
            val d = directions[moves % directions.size]
            curr = curr.map {
                when (d) {
                    Direction.L -> map.get(it)!!.left
                    Direction.R -> map.get(it)!!.right
                }
            }.toSet()
            val zCount = curr.count { it.endsWith('Z') }
            if (zCount>bestZCount) {
                bestZCount = zCount
                println("move #${moves} -- $curr -> $bestZCount")
            }
            moves++
        }
        return moves
    }

    // tests
    check(part1(readTestInput(1)) == 2)
    check(part1(readTestInput(2)) == 6)
    check(part2(readTestInput(3)) == 6)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

