package day10

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

val charPairs = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>'
)

fun pointsPart1(char: Char): Int =
    mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    ).getOrDefault(char, 0)

fun pointsPart2(char: Char): Int =
    mapOf(
        '(' to 1,
        '[' to 2,
        '{' to 3,
        '<' to 4,
    ).getOrDefault(char, 0)

tailrec fun parseBrackets(remainingLine: String, bracketStack: List<Char>): Pair<String, List<Char>> =
    if (remainingLine.isEmpty())
        Pair("", bracketStack)
    else if (bracketStack.isEmpty() && !charPairs.containsValue(remainingLine.first()))
        parseBrackets(remainingLine.takeLast(remainingLine.length - 1), listOf(remainingLine.first()))
    else if (bracketStack.isNotEmpty() && remainingLine.first() == charPairs[bracketStack.last()])
        parseBrackets(remainingLine.takeLast(remainingLine.length - 1), bracketStack.take(bracketStack.size - 1))
    else if (charPairs.containsValue(remainingLine.first()))
        Pair(remainingLine, bracketStack)
    else
        parseBrackets(remainingLine.takeLast(remainingLine.length - 1), bracketStack + remainingLine.first())


fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            parseBrackets(line, listOf()).first
        }
            .filter { it.isNotEmpty() }
            .sumOf { result -> pointsPart1(result.first()) }

    fun part2(input: List<String>): Long =
        input.map { line ->
            parseBrackets(line, listOf())
        }
            .filter { it.first.isEmpty() }
            .map { (_, chars) ->
                chars.map { pointsPart2(it).toLong() }.reduceRight { c, acc -> c + (acc * 5) }
            }
            .sorted()
            .let { it[it.size / 2] }

    val testInput = readInput("day10/Day10_test")
    testAnswer(part1(listOf(")")), 3)
    testAnswer(part1(testInput), 26397)
    testAnswer(part2(testInput), 288957)

    val input = readInput("day10/Day10")
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
