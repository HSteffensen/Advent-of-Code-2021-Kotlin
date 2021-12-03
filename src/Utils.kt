import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()
fun List<String>.asInts(): List<Int> {
    return map { it.toInt() }
}
inline fun <reified T> finalAnswerIsNotWrong(answer: T, wrongAnswers: List<T>): T {
    return answer
        .also {
            check(!wrongAnswers.contains(it)) { "Wrong answer for part 1: $answer" }
        }
}
inline fun <reified T> testAnswer(answer: T, expectedAnswer: T) {
    check(answer == expectedAnswer) { "wrong test. expected: $expectedAnswer, got: $answer" }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
