package day22

import finalAnswerIsNotWrong
import overlap
import readInput
import testAnswer
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

data class OnOffCuboid(
    val isOn: Boolean,
    val xStart: Int, val xEnd: Int,
    val yStart: Int, val yEnd: Int,
    val zStart: Int, val zEnd: Int,
) {
    val volume: Long = (xEnd - xStart + 1).toLong() *
            (yEnd - yStart + 1).toLong() *
            (zEnd - zStart + 1).toLong()

    fun overlaps(other: OnOffCuboid): Boolean =
        overlap(xStart, xEnd, other.xStart, other.xEnd) &&
                overlap(yStart, yEnd, other.yStart, other.yEnd) &&
                overlap(zStart, zEnd, other.zStart, other.zEnd)

    fun splitFromOverlap(other: OnOffCuboid): Collection<OnOffCuboid> =
        if (!overlaps(other))
            listOf(this)
        else
            listOfNotNull(
                createIfValidDimensions(isOn, xStart, other.xStart - 1, yStart, yEnd, zStart, zEnd),
                createIfValidDimensions(isOn, other.xEnd + 1, xEnd, yStart, yEnd, zStart, zEnd),
                createIfValidDimensions(
                    isOn, max(other.xStart, xStart), min(other.xEnd, xEnd),
                    yStart, other.yStart - 1, zStart, zEnd
                ),
                createIfValidDimensions(
                    isOn, max(other.xStart, xStart), min(other.xEnd, xEnd),
                    other.yEnd + 1, yEnd, zStart, zEnd
                ),
                createIfValidDimensions(
                    isOn, max(other.xStart, xStart), min(other.xEnd, xEnd),
                    max(other.yStart, yStart), min(other.yEnd, yEnd),
                    zStart, other.zStart - 1
                ),
                createIfValidDimensions(
                    isOn, max(other.xStart, xStart), min(other.xEnd, xEnd),
                    max(other.yStart, yStart), min(other.yEnd, yEnd),
                    other.zEnd + 1, zEnd
                ),
            )

    companion object {
        fun createIfValidDimensions(
            isOn: Boolean,
            xStart: Int, xEnd: Int,
            yStart: Int, yEnd: Int,
            zStart: Int, zEnd: Int
        ): OnOffCuboid? =
            if (xStart <= xEnd && yStart <= yEnd && zStart <= zEnd)
                OnOffCuboid(isOn, xStart, xEnd, yStart, yEnd, zStart, zEnd)
            else
                null
    }
}

fun parseInput(input: List<String>): List<OnOffCuboid> =
    input.map { line ->
        """(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""".toRegex()
            .find(line)!!
            .groupValues
            .let { matches ->
                OnOffCuboid(
                    matches[1] == "on",
                    matches[2].toInt(), matches[3].toInt(),
                    matches[4].toInt(), matches[5].toInt(),
                    matches[6].toInt(), matches[7].toInt()
                )
            }
    }

tailrec fun countOnLights(
    remainingCuboids: List<OnOffCuboid>,
    previousCuboids: List<OnOffCuboid> = listOf(),
): Long =
    if (remainingCuboids.isEmpty())
        previousCuboids.sumOf { it.volume }
    else
        countOnLights(
            remainingCuboids.drop(1),
            previousCuboids.flatMap { it.splitFromOverlap(remainingCuboids.first()) } +
                    if (remainingCuboids.first().isOn)
                        listOf(remainingCuboids.first())
                    else
                        listOf(),
        )

fun main() {
    fun part1(input: List<String>) =
        countOnLights(parseInput(input).filter { it.overlaps(OnOffCuboid(true, -50, 50, -50, 50, -50, 50)) })

    fun part2(input: List<String>) =
        countOnLights(parseInput(input))

    val testInput1 = readInput("day22/test1")
    testAnswer(part1(testInput1), 39).also { println("Test part 1 passed") }
//    testAnswer(part2(testInput1), 0).also { println("Test part 2 passed") }

    val testInput2 = readInput("day22/test2")
    testAnswer(part1(testInput2), 590784).also { println("Test part 1 passed") }
//    testAnswer(part2(testInput2), 0).also { println("Test part 2 passed") }

    val testInput3 = readInput("day22/test3")
    testAnswer(part1(testInput3), 474140).also { println("Test part 1 passed") }
    testAnswer(part2(testInput3), 2758514936282235).also { println("Test part 2 passed") }

    val input = readInput("day22/input")
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
