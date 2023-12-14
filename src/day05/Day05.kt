package day05

import kotlin.time.measureTimedValue
import logDuration
import logUsefulDuration
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

fun Almap.findMappingFor(currentId: Long): Long {
    val r = ranges.firstOrNull { r: AlmapRange -> r.srcRange.contains(currentId) }
    return if (r != null) {
        val idxInRange = currentId - r.srcRange.first
        r.destRange.first + idxInRange
    } else {
        currentId
    }
}

val catsForFindLocation = Category.entries.dropLast(1)
fun Almanac.findLocation(seed: Long): Long {
    var currentId = seed
    catsForFindLocation.forEach { src ->
        val map = this.findMapBySource(src)
        currentId = map.findMappingFor(currentId)
    }
    return currentId
}

fun Almanac.seedsAsRanges(): List<LongRange> {
    check(this.seeds.size % 2 == 0) { -> "Conversions of list of numbers to ranges requires an even number of elements" }
    return this.seeds.chunked(2)
        .map {
            LongRange(it.first(), it.first() + it.last())
        }
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
                        // range end is "inclusive", but nums[2] is our length, so -1
                        LongRange(nums[0], nums[0] + nums[2] - 1),
                        LongRange(nums[1], nums[1] + nums[2] - 1)
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
        val almanac = parse(input)
        // yikes that's a hell of a long list
        val ranges = almanac.seedsAsRanges()
        // flatmapping the entire ranges in memory yields an OOM
        // forEach might work but is also extremely slow
        // now replacing all large map operations by for-each which we can interrupt if needed
        var smallestLocationFound = Long.MAX_VALUE
        ranges.forEach { range ->
            range.forEach { seed ->
                // there's still quite a few that go beyond 100us or even several ms... yikes
                val location = logUsefulDuration({ -> almanac.findLocation(seed) }, { _ -> "find location took " })
                // we can't really interrupt here, because there's no guarantee the next seed will find a location that's higher or lower
                if (location < smallestLocationFound) {
                    smallestLocationFound = location
                }
            }
        }
        return smallestLocationFound
    }

    // quick check that no ranges overlap
    fun checkNoRangesOverlap(almanac: Almanac) {
        fun overlap(r1: LongRange, r2: LongRange): Boolean {
            return r1.contains(r2.first)
                    || r2.contains(r1.first)
                    || r1.contains(r2.last)
                    || r2.contains(r1.last)

        }

        fun checkRanges(ranges: List<LongRange>) {
            ranges.forEach { r1: LongRange ->
                ranges.forEach { r2: LongRange ->
                    check(r1 == r2 || !overlap(r1, r2)) { -> "Range ${r1} intersects with ${r2}" }
                }
            }
        }
        logDuration(
            { ->
                almanac.maps.forEach { map ->
                    checkRanges(map.ranges.map { it.srcRange })
                    checkRanges(map.ranges.map { it.destRange })
                }
            }, { _ -> "check maps ranges overlap took " })
        logDuration({ -> checkRanges(almanac.seedsAsRanges()) },
            { _ -> "checking seeds ranges overlap took " })
    }

    // tests
    check(part1(readTestInput(1)) == 35L)
    check(part2(readTestInput(1)) == 46L)

    val input = readDayInput()
    checkNoRangesOverlap(parse(input))
    printResult(1, measureTimedValue { part1(input) })
    printResult(2, measureTimedValue { part2(input) })
}
