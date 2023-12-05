fun main() {
    fun part1(input: List<String>): Int {
        return input.map { line: String ->
            val digits = line.toCharArray().filter(Character::isDigit)
            // lol
            val i: Int = (digits.first().digitToInt() * 10) + digits.last().digitToInt()
            // println("line = ${line} --> ${i}")
            i
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.size
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
