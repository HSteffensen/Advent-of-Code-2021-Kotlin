package day05

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.min
import kotlin.math.max
import kotlin.math.sign

fun Pair<Int, Int>.to(other: Pair<Int, Int>): List<Pair<Int, Int>> =
    when {
        this.first == other.first -> (min(this.second, other.second)..max(this.second, other.second)).map { Pair(this.first, it) }
        this.second == other.second -> (min(this.first, other.first)..max(this.first, other.first)).map { Pair(it, this.second) }
        else -> IntProgression.fromClosedRange(this.first, other.first, sign((other.first - this.first).toDouble()).toInt())
            .zip(IntProgression.fromClosedRange(this.second, other.second, sign((other.second - this.second).toDouble()).toInt()))
    }


fun main() {

    fun part1(input: List<String>): Int =
        input.map { line -> line.split(" -> ").map {
                point -> point.split(",").map{ it.toInt() }.let { Pair(it[0], it[1]) } }.let { Pair(it[0], it[1]) }
        }
            .filter { it.first.first == it.second.first || it.first.second == it.second.second }
            .flatMap { it.first.to(it.second) }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .count()

    fun part2(input: List<String>): Int =
        input.map { line -> line.split(" -> ").map {
                point -> point.split(",").map{ it.toInt() }.let { Pair(it[0], it[1]) } }.let { Pair(it[0], it[1]) }
        }
            .flatMap { it.first.to(it.second) }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .count()

    val testInput = readInput("day05/Day05_test")
    testAnswer(part1(testInput), 5)
    testAnswer(part2(testInput), 12)

    val input = readInput("day05/Day05")
    val wrongPart1Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part1(input), wrongPart1Answers))

    val wrongPart2Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part2(input), wrongPart2Answers))
}
