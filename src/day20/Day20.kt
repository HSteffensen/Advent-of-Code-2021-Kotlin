package day20

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

typealias Point = Pair<Int, Int>

fun Point.neighborhood(): List<Point> =
    (-1..1).flatMap { dy ->
        (-1..1).map { dx ->
            Point(first + dx, second + dy)
        }
    }

fun IntRange.expand(): IntRange =
    (first - 1)..(last + 1)

fun IntRange.shrink(): IntRange =
    (first + 1) until last

data class ImageEnhancement(
    val enhancementAlgorithm: Set<Int>,
    val grid: Set<Point>,
    val step: Int = 0
) {
    private val xRange = (grid.minOf { it.first } - 1)..(grid.maxOf { it.first } + 1)
    private val yRange = (grid.minOf { it.second } - 1)..(grid.maxOf { it.second } + 1)

    private fun currentValue(point: Point): Boolean =
        if (enhancementAlgorithm.contains(0) && step % 2 == 1 &&
            (!xRange.shrink().contains(point.first) || !yRange.shrink().contains(point.second))
        )
            true
        else
            grid.contains(point)

    private fun enhancePoint(point: Point): Boolean =
        enhancementAlgorithm.contains(
            point.neighborhood()
                .map { if (currentValue(it)) 1 else 0 }
                .reduce { a, b -> (a * 2) + b }
        )

    fun enhancementStep(): ImageEnhancement =
        ImageEnhancement(
            enhancementAlgorithm,
            yRange.flatMap { y ->
                xRange.map { x ->
                    Point(x, y)
                }.filter { enhancePoint(it) }
            }.toSet(),
            step + 1
        )

    override fun toString(): String =
        "Step $step in range (x=$xRange, y=$yRange)\n" +
                yRange.expand().joinToString("\n") { y ->
                    xRange.expand().joinToString("") { x ->
                        if (currentValue(Point(x, y)))
                            "#"
                        else
                            "."
                    }
                }
}

fun parseInputEnhancementLine(line: String): Set<Int> =
    line.mapIndexedNotNull { index, c -> if (c == '#') index else null }.toSet()

fun parseInputGrid(lines: List<String>): Set<Point> =
    lines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') Pair(x, y) else null
        }
    }.toSet()

fun parseInput(input: List<String>): Pair<Set<Int>, Set<Point>> =
    Pair(
        parseInputEnhancementLine(input.first()),
        parseInputGrid(input.drop(2))
    )

tailrec fun enhanceRepeat(image: ImageEnhancement, times: Int = 1): ImageEnhancement =
    if (times == 0)
        image
    else
        enhanceRepeat(
            image.enhancementStep(),
            times - 1
        )

fun main() {
    fun part1(input: List<String>, times: Int = 2): Int =
        parseInput(input).let { enhanceRepeat(ImageEnhancement(it.first, it.second), times) }.grid.size

    fun part2(input: List<String>): Int =
        part1(input, 50)

    val testInput = readInput("day20/Day20_test")
    testAnswer(part1(testInput), 35).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 3351).also { println("Test part 2 passed") }

    val input = readInput("day20/Day20")
    val wrongPart1Answers = listOf<Int>(
        5424,
        5560,
        5559,
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
