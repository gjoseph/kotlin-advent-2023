package day05

import printResult
import readDayInput
import readTestInput
import requireSize
import splitWhen
import toLongs

enum class Category { seed, soil, fertilizer, water, light, temperature, humidity, location }
data class AlmapRange(val destRange: LongRange, val srcRange: LongRange)
data class Almap(val source: Category, val destination: Category, val ranges: List<AlmapRange>)
data class Almanac(val seeds: List<Long>, val maps: List<Almap>)

fun Almanac.findMapBySource(src: Category) = maps.single { m -> m.source == src }
fun Almanac.findMapByDestination(dest: Category) = maps.single { m -> m.destination == dest }
fun Almap.findDestMappingFor(currentId: Long): Long {
    return ranges.filter { r -> r.srcRange.contains(currentId) }.map { r ->
        val idxInRange = currentId - r.srcRange.first
        r.destRange.first + idxInRange
    }.singleOrNull() ?: currentId
}

fun Almanac.findLocation(seed: Long): Long {
    var currentId = seed
    Category.entries.dropLast(1).forEachIndexed { i, src ->
        val map = this.findMapBySource(src)
        check(map.destination == Category.entries[i + 1])
        currentId = map.findDestMappingFor(currentId)
    }
    return currentId
}

fun parse(input: List<String>): Almanac {
    val seeds: List<Long> = input.first().substringAfter(":").toLongs()
    // skip first line (seeds) and blank line following it
    val maps = input.drop(2)
        .splitWhen { it.isEmpty() }
        .map { mapStr: List<String> ->
            check(mapStr.first().endsWith(" map:"))
            // first line has name/categories
            val (src, dest) = mapStr.first()
                .substringBefore(" map:")
                .split("-to-").requireSize(2)
                .map(Category::valueOf)
            // subsequent line define ranges
            val ranges = mapStr.drop(1)
                .map { s -> s.toLongs().requireSize(3) }
                .map { nums: List<Long> ->
                    AlmapRange(
                        LongRange(nums[0], nums[0] + nums[2]),
                        LongRange(nums[1], nums[1] + nums[2])
                    )
                }
            Almap(src, dest, ranges)
        }

    return Almanac(seeds, maps)
}

fun main() {

    fun part1(input: List<String>): Long {
        val almanac = parse(input)
        return almanac.seeds.map { seed -> almanac.findLocation(seed) }.min()
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    // tests
    check(part1(readTestInput(1)) == 35L)
    // uncomment when done with part 1 // check(part2(readTestInput(2)) == 0x00)

    val input = readDayInput()
    printResult(1, part1(input))
    printResult(2, part2(input))
}
