package day02

import readInput

fun main() {
    fun part1(input: List<String>): Int =
        input.map { it.split(" ") }.map { Pair(it[0], it[1].toInt()) }
            .map { when (it.first) {
                "forward" -> Pair(it.second, 0)
                "down" -> Pair(0, it.second)
                "up" -> Pair(0, -it.second)
                else -> { throw IllegalStateException("unexpected submarine instruction: ${it.first}") }
            } }
            .reduce { left, right -> Pair(left.first + right.first, left.second + right.second) }
            .run { first * second }

    fun part2(input: List<String>): Int =
        input.map { it.split(" ") }.map { Pair(it[0], it[1].toInt()) }
            .map { when (it.first) {
                "forward" -> Triple(it.second, 0, 0)
                "down" -> Triple(0, 0, it.second)
                "up" -> Triple(0, 0, -it.second)
                else -> { throw IllegalStateException("unexpected submarine instruction: ${it.first}") }
            } }
            .reduce { left, right -> Triple(left.first + right.first, left.second + (right.first * left.third), left.third + right.third) }
            .run { first * second }

    val testInput = readInput("day02/Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("day02/Day02")
    val wrongPart1Answers = listOf<Int>(
    )
    println(part1(input)
        .also { check(!wrongPart1Answers.contains(it)) { "Wrong answer for part 1: $it" } }
    )

    val wrongPart2Answers = listOf<Int>(
    )
    println(part2(input)
        .also { check(!wrongPart2Answers.contains(it)) { "Wrong answer for part 2: $it" } }
    )
}
