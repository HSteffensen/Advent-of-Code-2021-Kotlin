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

inline fun <reified T> T.println() = println(this)

inline fun <reified T> finalAnswerIsNotWrong(answer: T, wrongAnswers: List<T>): T =
    answer
        .also {
            check(!wrongAnswers.contains(it)) { "Wrong answer: $answer" }
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

    fun containsKey(position: Pair<Int, Int>): Boolean {
        return grid.containsKey(position)
    }

    fun toList() = grid.toList()

    fun map(transform: (T) -> T): SquareGrid<T> {
        val newGrid = SquareGrid<T>()
        grid.forEach { (position, value) ->
            newGrid[position] = transform(value)
        }
        return newGrid
    }

    companion object {
        inline fun <reified T> fromListOfLists(incoming: List<List<T>>): SquareGrid<T> {
            val squareGrid = SquareGrid<T>()
            incoming.forEachIndexed { y, row ->
                row.forEachIndexed { x, value ->
                    squareGrid[x, y] = value
                }
            }
            return squareGrid
        }

        inline fun <reified T> fromListOfPairs(incoming: List<Pair<Pair<Int, Int>, T>>): SquareGrid<T> {
            val squareGrid = SquareGrid<T>()
            incoming.forEach { (position, value) ->
                squareGrid[position] = value
            }
            return squareGrid
        }
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
