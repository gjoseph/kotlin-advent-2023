package day06

import kotlin.time.measureTimedValue
import multiply
import printResult
import readDayInput
import readTestInput
import requireSize
import toLongs

data class Race(val duration: Long, val recordDistance: Long)

fun main() {

    fun possibleRecordBreakingCount(race: Race): Int {
        // there's pbly a clever way to find a range without doing the math all the way through
        return LongRange(0, race.duration).map { hold ->
            val duration = race.duration
            val distTravelled = (duration - hold) * (hold)
            distTravelled > race.recordDistance
        }.count { it }
    }

    fun part1(input: List<String>): Int {
        val times = input.requireSize(2).first().substringAfter("Time:").toLongs()
        val distances = input.requireSize(2).last().substringAfter("Distance:").toLongs()
        val races = times.zip(distances).map { Race(duration = it.first, recordDistance = it.second) }
        // multiply nr of winners for each race
        return races.map { race ->
            possibleRecordBreakingCount(race)
        }.multiply()
    }

    fun part2(input: List<String>): Int {
        val time = input.requireSize(2).first().substringAfter("Time:").replace(" ", "").toLong()
        val distance = input.requireSize(2).last().substringAfter("Distance:").replace(" ", "").toLong()
        val race = Race(duration = time, recordDistance = distance)
        return possibleRecordBreakingCount(race)
    }

    // tests
    check(part1(readTestInput(1)) == 288)
    // uncomment when done with part 1 // check(part2(readTestInput(2)) == 0x00)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

