import java.util.regex.MatchResult
import java.util.regex.Pattern

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
            fromDigits(digits.first().digitToInt(), digits.last().digitToInt(), line)
        }.sum()
    }


    val NUMBERS_PATTERN = Pattern.compile("(\\d|one|two|three|four|five|six|seven|eight|nine)")
    val NUMBERS = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun stringToInt(it: String) = if (NUMBERS.contains(it)) NUMBERS.indexOf(it) + 1 else it.toInt()

    fun forLine(s: String): Int {
        // how elegant
        val nums = NUMBERS_PATTERN.matcher(s).results()
            .map(MatchResult::group)
            .map(::stringToInt).toList()
        return fromDigits(nums.first(), nums.last(), s)
    }

    fun part2(input: List<String>): Int {
        return input.map(::forLine).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day01_part1_test")
    check(part1(testInput1) == 142)
    val testInput2 = readInput("Day01_part2_test")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
