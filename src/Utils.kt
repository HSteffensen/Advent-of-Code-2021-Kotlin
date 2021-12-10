import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

fun commaSeparatedInts(input: List<String>, delimiter: String = ","): List<Int> =
    input.single().split(delimiter).asInts()

fun List<String>.asInts(): List<Int> {
    return map { it.toInt() }
}

inline fun <reified T> finalAnswerIsNotWrong(answer: T, wrongAnswers: List<T>): T =
    answer
        .also {
            check(!wrongAnswers.contains(it)) { "Wrong answer for part 1: $answer" }
        }

inline fun <reified T> testAnswer(answer: T, expectedAnswer: T) =
    check(answer == expectedAnswer) { "wrong test. expected: $expectedAnswer, got: $answer" }

class SquareGrid<T> {
    private val grid = mutableMapOf<Pair<Int, Int>, T>()

    operator fun set(position: Pair<Int, Int>, value: T) {
        grid[position] = value
    }

    operator fun set(x: Int, y: Int, value: T) {
        grid[Pair(x, y)] = value
    }

    operator fun get(position: Pair<Int, Int>): T? {
        return grid[position]
    }

    operator fun get(x: Int, y: Int): T? {
        return grid[Pair(x, y)]
    }

    fun toList() = grid.toList()

    companion object {
        inline fun <reified T> fromListOfLists(incoming: List<List<T>>): SquareGrid<T> {
            val grid = SquareGrid<T>()
            incoming.forEachIndexed { y, row ->
                row.forEachIndexed { x, value ->
                    grid[x, y] = value
                }
            }
            return grid
        }
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
