package day09

import kotlin.time.measureTimedValue
import printResult
import readDayInput
import readTestInput
import toInts

fun main() {
    fun processLine(initialLine: List<Int>): Int {
        val colls = mutableListOf<List<Int>>(initialLine)
        while (!colls.last().all { it == 0 }) {
            colls.add(colls.last().zipWithNext { a, b -> b - a })
        }
        return colls.map { it.last() }.sum()
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.toInts() }
            .map(::processLine)
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toInts() }
            .map { it.asReversed() }
            .map(::processLine)
            .sum()
    }

    // tests
    check(part1(readTestInput(1)) == 114)
    check(part2(readTestInput(1)) == 2)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

