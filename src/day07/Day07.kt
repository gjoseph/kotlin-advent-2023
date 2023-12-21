package day07

import kotlin.time.measureTimedValue
import printResult
import readDayInput
import readTestInput
import requireSize
import splitBySpace
import toPair

enum class Card(val c: Char) {
    A('A'), K('K'), Q('Q'), J('J'), T('T'), _9('9'), _8('8'), _7('7'), _6('6'), _5('5'), _4('4'), _3('3'), _2('2')
}

enum class HandType {
    FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIRS, ONE_PAIR, HIGH_CARD
}

data class Hand(val cards: List<Card>, val bid: Int) : Comparable<Hand> {
    private val comparator: Comparator<Hand> =
        compareBy<Hand> { it.type() }.thenComparing({ it.cards }, ListComparator())

    fun type(): HandType {
        val groupBy: List<Pair<Card, Int>> =
            cards.groupBy { it }.map { (k, v) -> Pair(k, v.size) }.sortedByDescending { it.second }
        return when (groupBy[0].second) {
            5 -> HandType.FIVE_OF_A_KIND
            4 -> HandType.FOUR_OF_A_KIND
            3 -> if (isFullHouse(groupBy)) HandType.FULL_HOUSE else HandType.THREE_OF_A_KIND
            2 -> if (hasTwoPairs(groupBy)) HandType.TWO_PAIRS else HandType.ONE_PAIR
            1 -> HandType.HIGH_CARD
            else -> error("wtf")
        }
    }

    private fun hasTwoPairs(cards: List<Pair<Card, Int>>): Boolean {
        return cards.filter { it.second == 2 }.size > 1
    }

    private fun isFullHouse(cards: List<Pair<Card, Int>>): Boolean {
        // we've already asserted there's 3-of-a-kind, therefore the only two remaining cards are the same (pair, so we have just two entries in this list) or not (three entries in this list)
        return cards.size == 2
    }

    override fun compareTo(other: Hand): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return "Hand(cards=$cards, type=${type()} bid=$bid)"
    }

}

fun card(c: Char): Card {
    return Card.entries.find { it.c == c }!!
}

fun main() {
    fun parse(input: List<String>): List<Hand> = input.map {
        val line = it.splitBySpace().requireSize(2).toPair()
        val cards: List<Card> = line.first.toCharArray().map { c -> card(c) }
        Hand(cards, line.second.toInt())
    }

    fun part1(input: List<String>): Int {
        return parse(input)
            .sortedDescending()
            .mapIndexed { i, h: Hand ->
                h.bid * (i + 1) // rank starts at 1, index starts at 0
            }.sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // tests
    check(HandType.FULL_HOUSE.compareTo(HandType.THREE_OF_A_KIND) < 0)
    check(HandType.ONE_PAIR.compareTo(HandType.THREE_OF_A_KIND) > 0)
    check(part1(readTestInput(1)) == 6440)
    // uncomment when done with part 1 // check(part2(readTestInput(2)) == 0x00)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

