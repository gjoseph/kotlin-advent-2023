package day05

import printResult
import readDayInput
import readTestInput
import requireSize
import toNumbers

enum class Category { seed, soil, fertilizer, water, light, temperature, humidity, location }
data class AlmapRange(val destRange: IntRange, val srcRange: IntRange)
data class Almap(val source: Category, val destination: Category, val ranges: List<AlmapRange>)
data class Almanac(val seeds: List<Int>, val maps: List<Almap>)

fun Almanac.findMapBySource(src: Category) = maps.single { m -> m.source == src }
fun Almanac.findMapByDestination(dest: Category) = maps.single { m -> m.destination == dest }
fun Almap.findDestMappingFor(currentId: Int): Int {
    return ranges.filter { r -> r.srcRange.contains(currentId) }.map { r ->
        val idxInRange = currentId - r.srcRange.first
        r.destRange.first + idxInRange
    }.singleOrNull() ?: currentId
}

fun parse(input: List<String>): Almanac {
    val seeds: List<Int> = input.first().substringAfter(":").toNumbers()
    // I tried hard -- and failed -- find an idiomatic way to do this -- scan, fold or one of these methods should probably work...
    val maps = mutableListOf<MutableList<String>>()
    var currentList = mutableListOf<String>()
    maps.add(currentList)
    // skip first line (seeds) and blank line following it
    input.drop(2).forEach { el ->
        if (el.isEmpty()) {
            currentList = mutableListOf()
            maps.add(currentList)
        } else {
            currentList.add(el)
        }
    }
    val almaps: List<Almap> = maps.map { mapStr: List<String> ->
        check(mapStr.first().endsWith(" map:"))
        // first line has name/categories
        val (src, dest) = mapStr.first().substringBefore(" map:").split("-to-").requireSize(2).map(Category::valueOf)
        // subsequent line define ranges
        val ranges = mapStr.drop(1)
            .map { s -> s.toNumbers().requireSize(3) }
            .map { ints: List<Int> ->
                AlmapRange(
                    IntRange(ints[0], ints[0] + ints[2]),
                    IntRange(ints[1], ints[1] + ints[2])
                )
            }
        Almap(src, dest, ranges)
    }

    return Almanac(seeds, almaps)
}

fun main() {
    fun findLocation(almanac: Almanac, currentId: Int): Int {
        var currentId1 = currentId
        Category.entries.dropLast(1).forEachIndexed { i, src ->
            val map = almanac.findMapBySource(src)
            println("map = ${map.source}->${map.destination}")
            check(map.destination == Category.entries[i + 1])
            currentId1 = map.findDestMappingFor(currentId1)
            println(" > ${currentId1}")
        }
        return currentId1
    }

    fun part1(input: List<String>): Int {
        return parse(input).seeds.map { seed -> findLocation(parse(input), seed) }.also { println(it) }.min()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // tests
    check(part1(readTestInput(1)) == 35)
    // uncomment when done with part 1 // check(part2(readTestInput(2)) == 0x00)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}
