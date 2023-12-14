package day04

import kotlin.math.pow
import printResult
import readDayInput
import readTestInput
import requireSize
import toInts

data class Card(val cardId: Int, val winningNumbers: Set<Int>, val myNumbers: Set<Int>) {
    fun matches() = this.winningNumbers.intersect(this.myNumbers).size
}

data class CardCount(val card: Card) {
    var count = 1
    fun plus(count: Int) {
        this.count += count
    }
}

fun main() {

    fun parseCard(input: String): Card {
        return input.split("Card", ":", "|").requireSize(4)
            .drop(1) // CBF working how to avoid the empty first element of this split
            .map { it.toInts().toSet() }
            .let { Card(it.get(0).single(), it.get(1), it.get(2)) }
    }

    fun score(c: Card): Int {
        return 2.toDouble().pow((c.matches() - 1)).toInt()
    }

    fun part1(input: List<String>): Int {
        return input
            .map(::parseCard)
            .map(::score)
            .sum()
    }

    fun part2(input: List<String>): Int {
        val cards = input.map(::parseCard).map { CardCount(it) }
        // each card's N matches grants me a copy of the N-next cards
        // set a counter for each card
        cards
            .forEach { cc ->
                // 3, 0 matches -> []
                // 4, 2 matches -> [5, 6]
                val cardsToAdd = 0.until(cc.card.matches()).map { it + cc.card.cardId + 1 }
                // this is likely going to cause ConcurrentModificationExceptions?
                cardsToAdd.forEach { cardId ->
                    cards.single { c -> c.card.cardId == cardId }.plus(cc.count)
                }
            }
        return cards.map { it.count }.sum()
    }

    // tests
    check(parseCard("Card 17: 1 22 3 | 2 5 22 1082 3 12") == Card(17, setOf(1, 22, 3), setOf(2, 5, 22, 1082, 3, 12)))
    check(score(parseCard("Card 17: 1 22 3 | 2 5 22 1082 3 12")) == 2)
    check(part1(readTestInput(1)) == 13)
    check(part2(readTestInput(1)) == 30)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}

