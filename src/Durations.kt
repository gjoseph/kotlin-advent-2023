import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

fun <T> logDuration(function: () -> T): T {
    return logDuration(function, { _ -> true }, { _ -> "function took " })
}

fun <T> logDuration(function: () -> T, message: (T) -> String): T {
    return logDuration(function, { _ -> true }, message)
}

fun <T> logUsefulDuration(function: () -> T, threshold: Duration): T {
    return logUsefulDuration(function, threshold, { _ -> "function took " })
}

fun <T> logUsefulDuration(function: () -> T, message: (T) -> String): T {
    return logDuration(function, { it > 1.milliseconds }, message)
}

fun <T> logUsefulDuration(function: () -> T, threshold: Duration, message: (T) -> String): T {
    return logDuration(function, { it > threshold }, message)
}

private fun <T> logDuration(function: () -> T, shouldLog: (Duration) -> Boolean, message: (T) -> String): T {
    val (res, duration) = measureTimedValue(function)
    if (shouldLog(duration)) {
        val s = if (message != null) message(res) else ""
        println("${s}${duration}")
    }
    return res
}
