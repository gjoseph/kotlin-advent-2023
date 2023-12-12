package day04

import kotlin.math.pow
import printResult
import readDayInput
import readTestInput
import requireSize


fun main() {
    data class Card(val winningNumbers: Set<Int>, val myNumbers: Set<Int>)

    fun parseCard(input: String): Card {
        fun numbers(s: String): Set<Int> {
            return s.split(Regex("\\s+")).map(String::toInt).toSet()
        }
        return input.split(':', '|').requireSize(3).drop(1)
            .map(String::trim)
            .map(::numbers)
            .let { Card(it.get(0), it.get(1)) }
    }

    fun score(c: Card): Int {
        val matchingNumbers = c.winningNumbers.intersect(c.myNumbers)
        return 2.toDouble().pow((matchingNumbers.size - 1)).toInt()
    }

    fun part1(input: List<String>): Int {
        return input
            .map(::parseCard)
            .map(::score)
            .sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // tests
    check(parseCard("Card 0: 1 22 3 | 2 5 22 1082 3 12") == Card(setOf(1, 22, 3), setOf(2, 5, 22, 1082, 3, 12)))
    check(score(parseCard("Card 0: 1 22 3 | 2 5 22 1082 3 12")) == 2)
    check(part1(readTestInput(1)) == 13)
    // uncomment when done with part 1 // check(part2(readTestInput(2)) == 0x00)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}

