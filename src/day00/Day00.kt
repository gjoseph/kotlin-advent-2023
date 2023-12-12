package day00

import printResult
import readDayInput
import readTestInput

fun main() {
    fun part1(input: List<String>): Int {
        TODO()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // tests
    check(part1(readTestInput(1)) == 0x00)
    // uncomment when done with part 1 // check(part2(readTestInput(2)) == 0x00)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}

