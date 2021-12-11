package day11

import SquareGrid
import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

fun Pair<Int, Int>.neighbors(): List<Pair<Int, Int>> =
    listOf(-1, 0, 1).flatMap { x ->
        listOf(-1, 0, 1).mapNotNull { y -> if (x == 0 && y == 0) null else Pair(x, y) }
    }
        .map { Pair(first + it.first, second + it.second) }

fun SquareGrid<Pair<Int, Boolean>>.sendFlashes(): SquareGrid<Pair<Int, Boolean>> =
    SquareGrid.fromListOfPairs(this.toList().flatMap { (position, value) ->
        if (value.first >= 9 && !value.second)
            position.neighbors()
                .filter { this.containsKey(it) }
                .map { Pair(it, Pair(1, false)) } + Pair(position, Pair(value.first, true))
        else
            listOf(Pair(position, value))
    }
        .groupBy { it.first }
        .map { (position, values) -> values.reduce { (_, a), (_, b) -> Pair(position, Pair(a.first + b.first, a.second || b.second)) } })

tailrec fun stepThroughOctopusesFlashCount(
    octopuses: SquareGrid<Pair<Int, Boolean>>,
    stepsRemaining: Int,
    flashCount: Int
): Int =
    if (stepsRemaining == 0)
        flashCount
    else if (octopuses.toList().none { (_, value) ->
            value.first >= 9 && !value.second
        })
        stepThroughOctopusesFlashCount(
            octopuses.map { (value, flashed) -> Pair(if (flashed) 0 else value + 1, false) },
            stepsRemaining - 1,
            flashCount + octopuses.toList().count { (_, value) -> value.second }
        )
    else
        stepThroughOctopusesFlashCount(
            octopuses.sendFlashes(),
            stepsRemaining,
            flashCount
        )

tailrec fun stepThroughOctopusesFlashSimultaneous(
    octopuses: SquareGrid<Pair<Int, Boolean>>,
    currentStep: Int = 1,
): Int =
    if (octopuses.toList().none { (_, value) ->
            value.first >= 9 && !value.second
        })
        if (octopuses.toList().count { (_, value) -> value.second } == 100)
            currentStep
        else
            stepThroughOctopusesFlashSimultaneous(
                octopuses.map { (value, flashed) -> Pair(if (flashed) 0 else value + 1, false) },
                currentStep + 1
            )
    else
        stepThroughOctopusesFlashSimultaneous(
            octopuses.sendFlashes(),
            currentStep
        )

fun main() {
    fun part1(input: List<String>, steps: Int = 100): Int =
        stepThroughOctopusesFlashCount(
            SquareGrid.fromListOfLists(
                input.map { line -> line.toCharArray().map { Pair(it.digitToInt(), false) } }
            ),
            steps,
            0)


    fun part2(input: List<String>): Int =
        stepThroughOctopusesFlashSimultaneous(
            SquareGrid.fromListOfLists(
                input.map { line -> line.toCharArray().map { Pair(it.digitToInt(), false) } }
            )
        )

    val testInput = readInput("day11/Day11_test")
    testAnswer(part1(testInput, 1), 0)
    testAnswer(part1(testInput, 2), 35)
    testAnswer(part1(testInput, 3), 35+45)
    testAnswer(part1(testInput, 4), 35+45+16)
    testAnswer(part1(testInput, 10), 204)
    testAnswer(part1(testInput), 1656)
    testAnswer(part2(testInput), 195)

    val input = readInput("day11/Day11")
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
