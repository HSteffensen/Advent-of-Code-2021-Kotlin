package day17

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.abs
import kotlin.math.sign
import kotlin.system.measureTimeMillis

fun parseInput(input: List<String>): Pair<IntRange, IntRange> =
    """target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""".toRegex()
        .find(input.single())!!
        .groupValues.asSequence().drop(1)
        .map { it.toInt() }
        .chunked(2)
        .map { it[0]..it[1] }
        .zipWithNext().single()

fun highestYReached(initialYVelocity: Int): Int =
    if (initialYVelocity <= 0)
        0
    else
        (initialYVelocity * (initialYVelocity + 1)) / 2

fun initialYVelocityReachesRange(yVelocity: Int, targetRange: IntRange, position: Int = 0): Boolean =
    if (targetRange.contains(position))
        true
    else if (position < targetRange.first && position < targetRange.last)
        false
    else
        initialYVelocityReachesRange(yVelocity - 1, targetRange, position + yVelocity)

fun initialXVelocityReachesRange(xVelocity: Int, targetRange: IntRange, position: Int = 0): Boolean =
    if (targetRange.contains(position))
        true
    else if ((position > targetRange.first && position > targetRange.last) || xVelocity == 0)
        false
    else
        initialXVelocityReachesRange(xVelocity - 1, targetRange, position + xVelocity)

fun xVelocitiesReachingRange(targetRange: IntRange): List<Int> =
    (0..targetRange.last).filter { initialXVelocityReachesRange(it, targetRange) }

fun yVelocitiesReachingRange(targetRange: IntRange): List<Int> =
    (-abs(targetRange.first)..abs(targetRange.first)).filter { initialYVelocityReachesRange(it, targetRange) }

fun velocityReachesRange(
    xVelocity: Int, yVelocity: Int,
    xTargetRange: IntRange, yTargetRange: IntRange,
    xPosition: Int = 0, yPosition: Int = 0
): Boolean =
    if (xTargetRange.contains(xPosition) && yTargetRange.contains(yPosition))
        true
    else if (xPosition > xTargetRange.last || yPosition < yTargetRange.first)
        false
    else
        velocityReachesRange(
            xVelocity - xVelocity.sign, yVelocity - 1,
            xTargetRange, yTargetRange,
            xPosition + xVelocity, yPosition + yVelocity
        )

fun velocitiesReachingRange(xTargetRange: IntRange, yTargetRange: IntRange): List<Pair<Int, Int>> =
    Pair(xVelocitiesReachingRange(xTargetRange), yVelocitiesReachingRange(yTargetRange))
        .let { (xVelocities, yVelocities) ->
            xVelocities.flatMap { dx ->
                yVelocities.filter { dy ->
                    velocityReachesRange(dx, dy, xTargetRange, yTargetRange)
                }.map { Pair(dx, it) }
            }
        }

fun main() {
    fun part1(input: List<String>): Int =
        highestYReached(abs(parseInput(input).second.first) - 1)

    fun part2(input: List<String>): Int =
        parseInput(input)
            .let { (xRange, yRange) ->
                velocitiesReachingRange(xRange, yRange)
            }.size

    val testInput = readInput("day17/Day17_test")
    testAnswer(parseInput(testInput), Pair(20..30, -10..-5)).also { println("Test parseInput passed") }
    testAnswer(part1(testInput), 45).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 112).also { println("Test part 2 passed") }

    val input = readInput("day17/Day17")
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
