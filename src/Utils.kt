import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun currentKtFile(): String = Thread.currentThread().stackTrace.last().fileName!!
fun inputFilePrefix() = Pattern.compile("\\.kt$").matcher(currentKtFile()).replaceAll("")
fun readTestInput(part: Int) = readInput("${inputFilePrefix()}_part${part}_test.txt")
fun readDayInput() = readInput("${inputFilePrefix()}.txt")
private fun readInput(fileName: String) = Path("src/${fileName}").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 * I mean, is it really?
 */
fun Any?.println() = println(this)

fun printResult(part: Int, res: Any?) {
    println("${inputFilePrefix()} - part $part : $res")
}
