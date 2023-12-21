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

open class Hand(val cards: List<Card>, val bid: Int) : Comparable<Hand> {
    open val comparator: Comparator<Hand> =
        compareBy<Hand> { it.type() }.thenComparing({ it.cards }, cardListComparator())

    private fun cardListComparator() = ListComparator<Card> { c1, c2 -> c1.compareTo(c2) }

    open fun type(): HandType {
        return typeOf(this.cards)
    }

    fun typeOf(cards: List<Card>): HandType {
        check(cards.isNotEmpty()) { "typeOf can only be used on a non-empty list" }
        val groupBy: Map<Card, Int> = cards.groupBy { it }.mapValues { it.value.size }
        val maxCount: Int = groupBy.maxBy { it.value }.value
        return when (maxCount) {
            5 -> HandType.FIVE_OF_A_KIND
            4 -> HandType.FOUR_OF_A_KIND
            3 -> if (isFullHouse(groupBy)) HandType.FULL_HOUSE else HandType.THREE_OF_A_KIND
            2 -> if (hasTwoPairs(groupBy)) HandType.TWO_PAIRS else HandType.ONE_PAIR
            1 -> HandType.HIGH_CARD
            else -> error("wtf")
        }
    }

    private fun hasTwoPairs(cardCounts: Map<Card, Int>): Boolean {
        return cardCounts.filter { it.value == 2 }.size > 1
    }

    private fun isFullHouse(cardCounts: Map<Card, Int>): Boolean {
        return cardCounts.values.toSet() == setOf(3, 2)
    }

    override fun compareTo(other: Hand): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return "Hand(cards=$cards, type=${type()} bid=$bid)"
    }
}

class HandWithJokerRule(cards: List<Card>, bid: Int) : Hand(cards, bid) {
    override fun type(): HandType {
        val withoutJokers = cards.filterNot { it == Card.J }
        val typeWithoutJokers = if (withoutJokers.isEmpty()) null else typeOf(withoutJokers)
        val jokerCount = cards.count { it == Card.J }
        // there aren't that many combinations so let's work then off. Bets are, this will be slow as with the real input
        val handType = when (jokerCount) {
            5 -> HandType.FIVE_OF_A_KIND
            4 -> HandType.FOUR_OF_A_KIND
            3 -> when (typeWithoutJokers) {
                HandType.ONE_PAIR -> HandType.FIVE_OF_A_KIND
                HandType.HIGH_CARD -> HandType.FOUR_OF_A_KIND
                else -> error("wtf")
            }

            2 -> when (typeWithoutJokers) {
                HandType.THREE_OF_A_KIND -> HandType.FIVE_OF_A_KIND
                HandType.ONE_PAIR -> HandType.FOUR_OF_A_KIND
                HandType.HIGH_CARD -> HandType.FULL_HOUSE
                else -> error("wtf")
            }

            1 -> when (typeWithoutJokers) {
                HandType.FOUR_OF_A_KIND -> HandType.FIVE_OF_A_KIND
                HandType.THREE_OF_A_KIND -> HandType.FOUR_OF_A_KIND
                HandType.TWO_PAIRS -> HandType.FULL_HOUSE
                HandType.ONE_PAIR -> HandType.THREE_OF_A_KIND
                HandType.HIGH_CARD -> HandType.ONE_PAIR
                else -> error("wtf")
            }

            0 -> typeWithoutJokers!!
            else -> error("wtf")
        }
        return handType
    }

    // Diminish J's value
    override val comparator: Comparator<Hand> =
        compareBy<Hand> { it.type() }.thenComparing({ it.cards }, ListComparator { c1, c2 ->
            jokerCompare(c1, c2)
        })

    fun jokerCompare(c1: Card, c2: Card): Int {
        val defaultCompare = c1.compareTo(c2)
        return if (defaultCompare != 0) {
            when (c1) {
                Card.J -> 1
                else -> when (c2) {
                    Card.J -> -1
                    else -> {
                        defaultCompare
                    }
                }
            }
        } else {
            0
        }
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
        return parse(input).sortedDescending().mapIndexed { i, h: Hand ->
            h.bid * (i + 1) // rank starts at 1, index starts at 0
        }.sum()
    }

    fun convertToJokerRule(it: Hand): HandWithJokerRule = HandWithJokerRule(it.cards, it.bid)

    fun part2(input: List<String>): Int {
        return parse(input).map(::convertToJokerRule).sortedDescending()
            .also {
                it.forEach {
                    println("cards = ${it.cards}  -> ${it.type()}")
                }
            }
            .mapIndexed { i, h: Hand ->
                h.bid * (i + 1) // rank starts at 1, index starts at 0
            }.sum()

    }

    // tests
    check(HandType.FULL_HOUSE.compareTo(HandType.THREE_OF_A_KIND) < 0)
    check(HandType.ONE_PAIR.compareTo(HandType.THREE_OF_A_KIND) > 0)
    check(Hand(listOf(Card.K, Card.T, Card.K, Card.T, Card.K), -1).type() == HandType.FULL_HOUSE)
    // recognise full house with joker-rule impl
    check(HandWithJokerRule(listOf(Card.K, Card.T, Card.K, Card.T, Card.K), -1).type() == HandType.FULL_HOUSE)
    // recognise full house with joker
    check(HandWithJokerRule(listOf(Card.K, Card.T, Card.J, Card.T, Card.K), -1).type() == HandType.FULL_HOUSE)
    // testing typeOf
    check(Hand(listOf(), -1).typeOf(listOf(Card.T, Card.T, Card.T)) == HandType.THREE_OF_A_KIND)
    // testing joker sort
    check(Card.K.compareTo(Card.J) < 0)
    check(Card.J.compareTo(Card.J) == 0)
    check(Card._9.compareTo(Card.J) > 0)
    check(Card._2.compareTo(Card.J) > 0)
    check(HandWithJokerRule(listOf(), -1).jokerCompare(Card.K, Card.Q) < 0)
    check(HandWithJokerRule(listOf(), -1).jokerCompare(Card.K, Card.J) < 0)
    check(HandWithJokerRule(listOf(), -1).jokerCompare(Card.J, Card.J) == 0)
    check(HandWithJokerRule(listOf(), -1).jokerCompare(Card._9, Card.J) < 0)
    check(HandWithJokerRule(listOf(), -1).jokerCompare(Card._2, Card.J) < 0)

    check(part1(readTestInput(1)) == 6440)
    check(part2(readTestInput(1)) == 5905)

    val input = readDayInput()
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}

