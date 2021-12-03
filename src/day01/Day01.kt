package day01

import asInts
import readInput

fun main() {
    fun part1(input: List<String>): Int =
        input.asInts()
            .zipWithNext { a, b -> b - a }
            .count { it > 0 }

    fun part2(input: List<String>): Int =
        input.asInts().windowed(3) { it.sum() }
            .zipWithNext { a, b -> b - a }
            .count { it > 0 }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day01/Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("day01/Day01")
    println(part1(input))

    val wrongPart2Answers = listOf(
        1659,
    )
    println(part2(input)
        .also { check(!wrongPart2Answers.contains(it)) { "Wrong answer for part 2: $it" } }
    )
}
