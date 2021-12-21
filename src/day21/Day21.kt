package day21

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

val d100Increasing = sequence {
    while (true) {
        yieldAll(1..100)
    }
}

data class GameState(
    val p1Pos: Int,
    val p2Post: Int,
    val p1Score: Int,
    val p2Score: Int
) {
    fun nextState(die: Sequence<Int>): GameState =
        TODO()
//        GameState(
//            p1Pos +
//        )
}

fun main() {
    fun part1(input: List<String>): Int =
        0

    fun part2(input: List<String>): Int =
        0

    val testInput = readInput("day21/Day21_test")
    testAnswer(part1(testInput), 739785).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 0).also { println("Test part 2 passed") }

    val input = readInput("day21/Day21")
    val wrongPart1Answers = listOf<Int>(
    )
    measureTimeMillis {
        println("Part 1: ${finalAnswerIsNotWrong(part1(input), wrongPart1Answers)}")
    }.also { println("\ttook $it milliseconds") }

    val wrongPart2Answers = listOf<Int>(
    )
    measureTimeMillis {
        println("Part 2: ${finalAnswerIsNotWrong(part2(input), wrongPart2Answers)}")
    }.also { println("\ttook $it milliseconds") }
}
