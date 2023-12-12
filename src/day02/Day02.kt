package day02

import java.util.EnumMap
import java.util.regex.Pattern
import printResult
import readDayInput
import readTestInput

enum class Cube(val maxCount: Int) {
    RED(12), GREEN(13), BLUE(14)
}

fun toCube(lowercaseName: String): Cube {
    return Cube.valueOf(lowercaseName.uppercase())
}

data class Game(
    var id: Int,
    val draws: List<Draws>
)

typealias Draws = EnumMap<Cube, Int>

val countAndCube = Pattern.compile("^(\\d+) (\\w+)$")

// Doing this with stream/collector just got too messy
inline fun Iterable<Int>.multiply(): Int {
    var res: Int = 1
    for (element in this) {
        res *= element
    }
    return res
}

fun main() {

    fun parseLine(line: String): Game {
        // Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        val gameId = line.substringAfter("Game ").substringBefore(":").toInt() // yolo, who needs input validation
        val draws: List<Draws> = line.substringAfter(":")
            .split(";").map { drawStr ->
                drawStr.split(",").map { countAndCubeStr ->
                    val matcher = countAndCube.matcher(countAndCubeStr.trim())
                    matcher.find()
                    val count = matcher.group(1).toInt()
                    val cube = toCube(matcher.group(2))
                    Pair(cube, count)
                }.toMap(Draws(Cube::class.java))
            }

        return Game(gameId, draws)
    }

    fun isPossible(draws: Draws): Boolean {
        return draws.all { (cube, count) -> count <= cube.maxCount }
    }

    fun isPossible(game: Game): Boolean {
        return game.draws.all(::isPossible)
    }

    fun part1(input: List<String>): Int {
        return input.map(::parseLine)
            .filter(::isPossible)
            .sumOf(Game::id)
    }

    fun maxForEachCube(g: Game) = Cube.entries.map { cube ->
        val map: List<Int> = g.draws.map { it: Draws -> it.getOrDefault(cube, 0) }
        val minBy = map.max()
        Pair(cube, minBy)
    }.toMap()

    fun part2(input: List<String>): Int {
        return input.map(::parseLine)
            .map(::maxForEachCube)
            .map { maxForEachCube: Map<Cube, Int> ->
                // Doing this with stream/collector just got too messy
                maxForEachCube.values.multiply()
            }.sum()
    }

    // tests
    check(part1(readTestInput(1)) == 8)
    check(part2(readTestInput(2)) == 2286)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}

