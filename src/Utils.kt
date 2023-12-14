import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.readLines

// === input/test files
fun inputFilePrefix(): String {
    // This is getting a bit tedious and magic, but
    val e = Thread.currentThread().stackTrace.filter { it.fileName != "AdventOfKode.kt" }.last()
    return e.className.substringBefore(".") + "/" + e.fileName.substringBefore(".kt")
}
fun readTestInput(part: Int) = readInput("${inputFilePrefix()}_part${part}_test.txt")
fun readDayInput() = readInput("${inputFilePrefix()}.txt")
private fun readInput(fileName: String): List<String> {
    val path = Path("src/${fileName}")
    if (path.notExists()) {
        path.createFile()
        error("Created ${path} for you 🙄")
    }
    return path.readLines()
}

// === other random utils

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

// Doing this with stream/collector just got too messy
fun Iterable<Int>.multiply(): Int {
    var res: Int = 1
    for (element in this) {
        res *= element
    }
    return res
}

fun <T> List<T>.requireSize(requiredSize: Int): List<T> {
    return when (size) {
        requiredSize -> this
        else -> throw IllegalArgumentException("List has ${size} element(s) but must have ${requiredSize}.")
    }
}

private fun String.splitBySpace() = this.trim().split(Regex("\\s+"))
fun String.toInts() = splitBySpace().map(String::toInt)
fun String.toLongs() = splitBySpace().map(String::toLong)

// ====== print & output
/**
 * The cleaner shorthand for printing output.
 * I mean, is it really?
 */
fun Any?.println() = println(this)

fun printResult(part: Int, res: Any?) {
    println("${inputFilePrefix()} - part $part : $res")
}
