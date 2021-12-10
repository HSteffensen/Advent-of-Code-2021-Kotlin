package day09

import SquareGrid
import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

fun SquareGrid<Int>.neighborsHeights(position: Pair<Int, Int>): List<Int> =
    listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        .mapNotNull { (dx, dy) -> this[position.first + dx, position.second + dy] }

fun neighborsPositions(position: Pair<Int, Int>): List<Pair<Int, Int>> =
    listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        .map { (dx, dy) -> Pair(position.first + dx, position.second + dy) }

fun SquareGrid<Int>.findBasin(
    position: Pair<Int, Int>,
    knownBasin: Set<Pair<Int, Int>> = setOf()
): Set<Pair<Int, Int>> =
    if (this[position] == 9 || knownBasin.contains(position))
        knownBasin
    else
        neighborsPositions(position)
            .filter { !knownBasin.contains(it) && this[it] != null }
            .fold(knownBasin.plus(position)) { currentKnownBasin, nextPosition ->
                findBasin(nextPosition, currentKnownBasin)
            }

fun main() {
    fun part1(input: List<String>): Int =
        SquareGrid.fromListOfLists(input.map { line -> line.map { it.digitToInt() } })
            .let { grid ->
                grid.toList().filter { (position, value) ->
                    (grid.neighborsHeights(position).minOrNull() ?: 999) > value
                }
            }
            .sumOf { (_, value) -> value + 1 }

    fun part2(input: List<String>): Int =
        SquareGrid.fromListOfLists(input.map { line -> line.map { it.digitToInt() } })
            .let { grid ->
                grid.toList().filter { (position, value) ->
                    (grid.neighborsHeights(position).minOrNull() ?: 999) > value
                }
                    .map { (position, _) -> grid.findBasin(position).size }
            }
            .sortedDescending()
            .take(3)
            .reduce { a, b -> a * b }

    val testInput = readInput("day09/Day09_test")
    testAnswer(part1(testInput), 15)
    testAnswer(part2(testInput), 1134)

    val input = readInput("day09/Day09")
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
