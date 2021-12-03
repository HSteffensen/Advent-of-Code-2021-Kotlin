package day03

import finalAnswerIsNotWrong
import readInput
import testAnswer

fun main() {
    fun part1(input: List<String>): Int =
        input.first().indices
            .map { index ->
                input.sortedBy { it[index] }[input.size/2][index].digitToInt()
            }
            .fold(Pair(0, 0)) { left, right -> Pair((left.first * 2) + right, (left.second * 2) + 1 - right) }
            .toList().reduce { a, b -> a * b }

    fun part2(input: List<String>): Int =
        input.partition { it[0] == input.sorted()[input.size/2][0] }.toList()
            .zip(listOf(Pair({ a: Int -> -a }, 1),Pair({ a: Int -> a }, 0))) { a, b -> Triple(a, b.first, b.second) }
            .map { (part, sorter, preference) ->
                part.first().indices.fold(part) { remaining, index ->
                    remaining.filter { measurement ->
                        measurement[index] == listOf('0', '1')
                            .map { char -> Pair(char, remaining.count { it[index] == char }) }
                            .filter { it.second > 0 }
                            .sortedBy { if (it.first == preference.digitToChar()) 0 else 1 }
                            .sortedBy{ sorter(it.second) }[0].first
                    }
                }
                .first().map { it.digitToInt() }
                .reduce { left, right -> (left * 2) + right }
            }
            .reduce { a, b -> a * b }

    val testInput = readInput("day03/Day03_test")
    testAnswer(part1(testInput), 198)
    testAnswer(part2(testInput), 230)

    val input = readInput("day03/Day03")
    val wrongPart1Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part1(input), wrongPart1Answers))

    val wrongPart2Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part2(input), wrongPart2Answers))
}
