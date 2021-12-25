package day25

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.abs
import kotlin.math.sign
import kotlin.system.measureTimeMillis

data class Point(val x: Int, val y: Int)

data class CucumberHerds(val eastHerd: List<Point>,
                         val southHerd: List<Point>,
                         val width: Int,
                         val height: Int)

fun findSeaCucumbers(input: List<String>, cucumber: Char): List<Point> =
    input.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == cucumber)
                Point(x, y)
            else
                null
        }
    }

fun parseInput(input: List<String>): CucumberHerds =
    CucumberHerds(
        findSeaCucumbers(input, '>'),
        findSeaCucumbers(input, 'v'),
        input.first().length,
        input.size
    )

fun moveEastHerd(eastHerd: List<Point>,
                 southHerd: List<Point>,
                 width: Int): List<Point> =
    eastHerd.map { eCucumber ->
        if ((southHerd + eastHerd).any { otherCucumber ->
            otherCucumber.x == (eCucumber.x + 1) % width && otherCucumber.y == eCucumber.y
        })
            eCucumber
        else
            Point((eCucumber.x + 1) % width, eCucumber.y)
    }

fun moveSouthHerd(eastHerd: List<Point>,
                 southHerd: List<Point>,
                 height: Int): List<Point> =
    southHerd.map { sCucumber ->
        if ((eastHerd + southHerd).any { otherCucumber ->
                sCucumber.x == otherCucumber.x && (sCucumber.y + 1) % height == otherCucumber.y
            })
            sCucumber
        else
            Point(sCucumber.x, (sCucumber.y + 1) % height)
    }

tailrec fun moveCucumbers(eastHerd: List<Point>, southHerd: List<Point>,
                  width: Int, height: Int,
                  prevEastHerd: List<Point> = listOf(), prevSouthHerd: List<Point> = listOf(), steps: Int = 0): Pair<CucumberHerds, Int> =
    if (eastHerd == prevEastHerd && southHerd == prevSouthHerd)
        Pair(CucumberHerds(eastHerd, southHerd, width, height), steps)
    else
        moveCucumbers(
            moveEastHerd(eastHerd, southHerd, width),
            moveSouthHerd(moveEastHerd(eastHerd, southHerd, width), southHerd, height),
            width,
            height,
            eastHerd,
            southHerd,
            steps + 1//.also { println("${steps}\n${printCucumbers(eastHerd, southHerd, width, height)}") }
        )

fun printCucumbers(eastHerd: List<Point>, southHerd: List<Point>, width: Int, height: Int) =
    (0..height).joinToString("\n") { y ->
        (0..width).joinToString("") { x ->
            if (eastHerd.contains(Point(x, y)))
                ">"
            else if (southHerd.contains(Point(x, y)))
                "v"
            else
                "."
        }
    }

fun main() {
    fun part1(input: List<String>): Int =
        parseInput(input).let { herds ->
            moveCucumbers(herds.eastHerd, herds.southHerd, herds.width, herds.height)
        }.second

    fun part2(input: List<String>): Int =
        0

    val testInput = readInput("day25/test")
    testAnswer(part1(testInput), 58).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 0).also { println("Test part 2 passed") }

    val input = readInput("day25/input")
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
