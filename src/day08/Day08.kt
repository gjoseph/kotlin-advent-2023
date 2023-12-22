package day08

import kotlin.time.measureTimedValue
import lcm
import printResult
import readDayInput
import readTestInput
import requireSize

enum class Direction { L, R }
data class Destinations(val left: String, val right: String)
data class Doc(val directions: List<Direction>, val map: Map<String, Destinations>) {

    fun nextStep(
        currentMoveCount: Int,
        currentLocation: String,
    ): String {
        val d = this.directions[currentMoveCount % this.directions.size]
        return when (d) {
            Direction.L -> map.get(currentLocation)!!.left
            Direction.R -> map.get(currentLocation)!!.right
        }
    }
}

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
        val doc = parse(input)
        var moves = 0
        var curr = "AAA"
        while (curr != "ZZZ") {
            curr = doc.nextStep(moves, curr)
            moves++
        }
        return moves
    }


    fun part2(input: List<String>): Long {
        val doc = parse(input)
        // solve each route individually, and hope the least common multiple is the right answer
        // also hope that each starting point leads to a _different_ endpoint ... (it does)
        // and... fuck me, why did my lcm() not blow up when it overflew
        var startingPoints = doc.map.filterKeys { it.endsWith('A') }.keys

        // Empirically, we've verified that all routes loop in on themselves once we reach the end
        data class Route(val initialMovesToDest: Int, val loopLength: Int)

        val results: List<Route> = startingPoints.map { start ->
            var moves = 0
            var curr = start
            val route = mutableListOf(curr)
            while (!curr.endsWith('Z')) {
                curr = doc.nextStep(moves, curr)
                moves++
                route.add(curr)
            }
            val next = doc.nextStep(moves, curr)
            check(route.contains(next)) { "Empirically, we've verified all routes are looping on themselves" }
            val startOfLoop = route.indexOf(next)
            val loopMoves = route.size - startOfLoop
            // ... but that turned out to be absolutely useless
            Route(moves, loopMoves)
        }
        // because in the end, all we needed was for lcm() to not overflow silently (i.e use Long instead of Int) and that was the answer... :facepalm:
        return lcm(results.map { it.initialMovesToDest.toLong() })
    }

    // tests
    check(part1(readTestInput(1)) == 2)
    check(part1(readTestInput(2)) == 6)
    check(part2(readTestInput(3)) == 6L)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

