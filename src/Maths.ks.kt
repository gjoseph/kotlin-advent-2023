// oh well https://www.baeldung.com/kotlin/gcd
fun gcd(a: Long, b: Long): Long {
    var num1 = a
    var num2 = b
    while (num2 != 0L) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}

fun lcm(a: Long, b: Long): Long {
    // with Ints, the below overflew silently (proven by tests)
    // with Longs, we're fine... but might as well use StrictMath while we're at it
    // return abs(a * b) / gcd(a, b)
    // return (abs(a) / gcd(a, b)) * abs(b)
    return StrictMath.floorDiv(StrictMath.abs(StrictMath.multiplyExact(a, b)), gcd(a, b))
}

fun lcm(nums: List<Long>): Long {
    return lcm(*nums.toLongArray())
}

fun lcm(vararg nums: Long): Long {
    require(nums.size > 0) { "Give me some numbers to crunch" }
    var res = 0L
    nums.forEachIndexed { i: Int, n: Long ->
        res = if (i == 0) nums[0] else lcm(res, nums[i])
    }
    return res
}

// tests
fun main() {
    check(gcd(54L, 24L) == 6L)
    check(lcm(2L, 3L) == 6L)
    check(lcm(3L, 5L, 7L) == 105L)
    // does it break with large numbers!? --> it did, when using Int, so switched to Long without looking at why/where things broke silently...then switch back the division used in lcm(Int,Int)
    check(lcm(300L, 450L, 75L) == 900L)
    check(lcm(3_000_000L, 4_500_000L, 750_000L) == 9_000_000L)
    // but this overflows with Ints regardless of which variation of lcm() we use
    check(lcm(300_000_000L, 450_000_000L, 75_000_000L) == 900_000_000L)
}
