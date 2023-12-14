package day01

import java.util.regex.Pattern
import kotlin.time.measureTimedValue
import printResult
import readDayInput
import readTestInput

fun main() {
    fun fromDigits(first: Int, last: Int, debug: String): Int {
        // woah maths
        val i: Int = (first * 10) + last
        // println("* ${debug} --> ${i}")
        return i
    }

    fun part1(input: List<String>): Int {
        return input.map { line: String ->
            val digits = line.toCharArray().filter(Character::isDigit)
            fromDigits(digits.first().digitToInt(), digits.last().digitToInt(), "part 1: $line")
        }.sum()
    }


    val NUMBERS_PATTERN = Pattern.compile("(\\d|one|two|three|four|five|six|seven|eight|nine)")
    val NUMBERS = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun stringToInt(it: String) = if (NUMBERS.contains(it)) NUMBERS.indexOf(it) + 1 else it.toInt()

    fun forLine(s: String): Int {
        // how elegant
        val matcher = NUMBERS_PATTERN.matcher(s)
        // bit of bullshit with the regex so we find merged words like oneight -> 1, 8
        var nextPos = 0
        val matches: MutableList<String> = mutableListOf()
        while (matcher.find(nextPos)) {
            matches.add(s.substring(matcher.start(), matcher.end()))
            nextPos = matcher.start() + 1
        }
        val nums = matches
            .map(::stringToInt).toList()
        return fromDigits(nums.first(), nums.last(), "part 2: $s")
    }

    fun part2(input: List<String>): Int {
        return input.map(::forLine).sum()
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readTestInput(1)) == 142)
    check(part2(readTestInput(2)) == 281)
    // found a bug, not covered by test in description
    check(part2(listOf("oneight")) == 18)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}
