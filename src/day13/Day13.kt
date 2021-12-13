package day13

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

fun parseInput(input: List<String>): Pair<Set<Pair<Int, Int>>, List<Pair<String, Int>>> =
    Pair(
        input.takeWhile { it != "" }
            .map { it.split(',') }
            .map { Pair(it[0].toInt(), it[1].toInt()) }.toSet(),
        input.takeLastWhile { it != "" }
            .map { it.split(' ').last().split('=') }
            .map { Pair(it[0], it[1].toInt()) }
    )

fun foldPaper(points: Set<Pair<Int, Int>>, fold: Pair<String, Int>): Set<Pair<Int, Int>> =
    points.map { (x, y) ->
        Pair(
            if (fold.first == "x" && x > fold.second)
                (2 * fold.second) - x
            else
                x,
            if (fold.first == "y" && y > fold.second)
                (2 * fold.second) - y
            else
                y
        )
    }.toSet()

fun printPoints(points: Set<Pair<Int, Int>>): String =
    "\n" + (0..points.maxOf { it.first }).map { x ->
        (0..points.maxOf { it.second }).map { y ->
            if (points.contains(Pair(x, y)))
                "#"
            else
                "."
        }.joinToString("")
    }.joinToString("\n") + "\n"

fun main() {
    fun part1(input: List<String>): Int =
        parseInput(input).let { (points, folds) ->
            foldPaper(points, folds.first())
        }.size

    fun part2(input: List<String>): String =
        printPoints(parseInput(input).let { (points, folds) ->
            folds.fold(points) { foldedPoints, fold -> foldPaper(foldedPoints, fold) }
                .map { (x, y) -> Pair(y, x) }.toSet()
        })

    val testInput = readInput("day13/Day13_test")
    testAnswer(part1(testInput), 17)
    testAnswer(
        part2(testInput), """
#####
#...#
#...#
#...#
#####
"""
    )

    val input = readInput("day13/Day13")
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
