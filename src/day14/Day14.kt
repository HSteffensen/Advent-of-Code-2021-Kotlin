package day14

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

typealias Counter<T> = Map<T, Long>

fun initialChainFromInput(input: List<String>): List<Char> =
    input.first().toList()

fun initialChainMapFromInput(input: List<String>): Counter<Pair<Char, Char>> =
    input.first().toList().zipWithNext().groupingBy { it }.eachCount().map { it.key to it.value.toLong() }.toMap()

fun pairsMapFromInput(input: List<String>): Map<Pair<Char, Char>, Char> =
    input.drop(2)
        .map { it.split(" -> ") }
        .associate { Pair(it[0][0], it[0][1]) to it[1].single() }

fun polymerPairInsertion(chain: List<Char>, pairsMap: Map<Pair<Char, Char>, Char>): List<Char> =
    chain.windowed(2, partialWindows = true)
        .flatMap {
            if (it.size == 1)
                it
            else
                listOf(it[0], pairsMap[Pair(it[0], it[1])]!!)
        }

fun polymerPairInsertion(
    chainMap: Counter<Pair<Char, Char>>,
    pairsMap: Map<Pair<Char, Char>, Char>
): Counter<Pair<Char, Char>> =
    chainMap.flatMap { (pair, count) ->
        listOf(
            Pair(pair.first, pairsMap[pair]!!) to count,
            Pair(pairsMap[pair]!!, pair.second) to count,
        )
    }
        .groupBy { it.first }
        .map { pairGroup -> pairGroup.key to pairGroup.value.sumOf { it.second } }
        .toMap()

tailrec fun runPolymerInsertionSteps(
    chainMap: Counter<Pair<Char, Char>>,
    pairsMap: Map<Pair<Char, Char>, Char>,
    stepsRemaining: Int
): Counter<Pair<Char, Char>> =
    if (stepsRemaining == 0)
        chainMap
    else
        runPolymerInsertionSteps(polymerPairInsertion(chainMap, pairsMap), pairsMap, stepsRemaining - 1)

tailrec fun runPolymerInsertionSteps(
    chain: List<Char>,
    pairsMap: Map<Pair<Char, Char>, Char>,
    stepsRemaining: Int
): List<Char> =
    if (stepsRemaining == 0)
        chain
    else
        runPolymerInsertionSteps(polymerPairInsertion(chain, pairsMap), pairsMap, stepsRemaining - 1)

fun main() {
    fun part1(input: List<String>, steps: Int = 10): Long =
        runPolymerInsertionSteps(initialChainMapFromInput(input), pairsMapFromInput(input), steps)
            .flatMap { listOf(it.key.first to it.value, it.key.second to it.value) }
            .let { it + (initialChainFromInput(input).first() to 1L) + (initialChainFromInput(input).last() to 1L) }
            .groupBy { it.first }
            .map { charCounts -> charCounts.key to (charCounts.value.sumOf { it.second }) / 2 }
            .let { countsList -> countsList.maxOf { it.second } - countsList.minOf { it.second } }

    fun part2(input: List<String>): Long =
        part1(input, 40)

    val testInput = readInput("day14/Day14_test")
    testAnswer(part1(testInput, 1), 1).also { println("Test passed") }
    testAnswer(part1(testInput), 1588).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 2188189693529).also { println("Test part 2 passed") }

    val input = readInput("day14/Day14")
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
