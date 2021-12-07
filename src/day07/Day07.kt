package day07

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.abs
import kotlin.system.measureTimeMillis


fun main() {

    fun part1(input: List<String>): Int =
        input.first().split(",").map { it.toInt() }
            .let { inputList ->
                (inputList.minOf { it }..inputList.maxOf { it })
                    .map { position -> inputList.sumOf { abs(position - it) } }
            }
            .minOf { it }

    fun part2(input: List<String>): Int =
        input.first().split(",").map { it.toInt() }
            .let { inputList ->
                (inputList.minOf { it }..inputList.maxOf { it })
                    .map { targetPosition ->
                        inputList.sumOf { crabPosition ->
                            abs(targetPosition - crabPosition).let { ((it + 1) * it) / 2 }
                        }
                    }
            }
            .minOf { it }

    val testInput = readInput("day07/Day07_test")
    testAnswer(part1(testInput), 37)
    testAnswer(part2(testInput), 168)

    val input = readInput("day07/Day07")
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
