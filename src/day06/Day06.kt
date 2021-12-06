package day06

import finalAnswerIsNotWrong
import readInput
import testAnswer


fun main() {

    fun part1(input: List<String>, days: Int = 80): Int =
        input.first().split(",").map { it.toInt() }
            .let { initialFish ->
                (1..days).fold(initialFish) { fish, _ ->
                    fish.flatMap { if (it == 0) listOf(6, 8) else listOf(it - 1) }
                }
            }
            .count()

    fun part2(input: List<String>, days: Int = 256): Long =
        input.first().split(",").map { it.toInt() }
            .groupingBy { it }
            .eachCount()
            .map { it.key to it.value.toLong() }
            .toMap()
            .let { initialFish ->
                (1..days).fold(initialFish) { fish, _ ->
                    fish.flatMap {
                        when (it.key) {
                            0 -> listOf(6 to it.value,8 to it.value)
                            else -> listOf(it.key - 1 to it.value)
                        }
                    }
                        .groupBy { it.first }
                        .map { group -> group.key to group.value.sumOf { it.second } }
                        .toMap()
                }
            }
            .toList()
            .sumOf { it.second }

    val testInput = readInput("day06/Day06_test")
    val input = readInput("day06/Day06")
    testAnswer(part1(testInput, 18), 26)
    testAnswer(part1(testInput), 5934)
    testAnswer(part2(testInput, 18), 26)
    testAnswer(part2(testInput, 80), 5934)
    testAnswer(part2(input, 80), 352151)
    println("Part2 works for part1 tests")
    testAnswer(part2(testInput), 26984457539)

//    val input = readInput("day06/Day06")
    val wrongPart1Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part1(input), wrongPart1Answers))

    val wrongPart2Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part2(input), wrongPart2Answers))
}
