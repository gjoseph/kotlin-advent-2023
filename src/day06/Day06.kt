package day06

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.measureTimedValue
import multiply
import printResult
import quadratic
import readDayInput
import readTestInput
import requireSize
import toLongs

data class Race(val duration: Long, val recordDistance: Long)

fun main() {

    fun isBreaking(hold: Long, race: Race): Boolean {
        val duration = race.duration
        val distTravelled = (duration - hold) * (hold)
        return distTravelled > race.recordDistance
    }

    fun possibleRecordBreakingCount(race: Race): Int {
        // there's pbly a clever way to find a range without doing the math all the way through
        return LongRange(0, race.duration).map { hold ->
            isBreaking(hold, race)
        }.count { it }
    }

    fun possibleRecordBreakingCountOptimised(race: Race): Int {
        // hold == speed
        // distance == (dur-hold) * speed
        // distance == (dur-hold) * hold
        // distance == (dur*hold) - hold^2
        // hold^2 - (dur*hold) + distance = 0
        // x == hold, a == 1, b == -dur, c == distance
        val q = quadratic(1, -race.duration, c = race.recordDistance + 1)
        // assuming first is always lowest boundary, which we have to round; round down upper boundary
        // effectively, this is Range(q.first, q.second).size
        return ((floor(q.second) - ceil(q.first)).toLong() + 1).toInt()
    }

    fun parse(input: List<String>): Pair<String, String> {
        val timeStr = input.requireSize(2).first().substringAfter("Time:")
        val distanceStr = input.requireSize(2).last().substringAfter("Distance:")
        return Pair(timeStr, distanceStr)
    }

    fun parsePart1(input: List<String>): List<Race> {
        val (timeStr, distanceStr) = parse(input)
        val times = timeStr.toLongs()
        val distances = distanceStr.toLongs()
        val races = times.zip(distances).map { Race(duration = it.first, recordDistance = it.second) }
        return races
    }

    fun part1(input: List<String>): Int {
        // multiply nr of winners for each race
        return parsePart1(input).map { race ->
            possibleRecordBreakingCount(race)
        }.multiply()
    }

    fun part1Optimised(input: List<String>): Int {
        return parsePart1(input).map(::possibleRecordBreakingCountOptimised).multiply()
    }

    fun parsePart2(input: List<String>): Race {
        val (timeStr, distanceStr) = parse(input)
        val time = timeStr.replace(" ", "").toLong()
        val distance = distanceStr.replace(" ", "").toLong()
        val race = Race(duration = time, recordDistance = distance)
        return race
    }

    fun part2(input: List<String>): Int {
        val race = parsePart2(input)
        return possibleRecordBreakingCount(race)
    }

    fun part2Optimised(input: List<String>): Int {
        val race = parsePart2(input)
        return possibleRecordBreakingCountOptimised(race)
    }

    // tests
    check(part1(readTestInput(1)) == 288)
    check(part2(readTestInput(1)) == 71503)
    check(part1Optimised(readTestInput(1)) == 288)
    check(part2Optimised(readTestInput(1)) == 71503)

    val input = readDayInput()
    // printResult(1, measureTimedValue { part1(input) })
    // printResult(2, measureTimedValue { part2(input) }) // takes about 800ms
    printResult(1, measureTimedValue { part1Optimised(input) }) // can be slightly slower than non-optimised
    printResult(2, measureTimedValue { part2Optimised(input) }) // takes about 30us
}

