package day12

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis

typealias CaveMap = Map<String, Set<String>>

fun String.isUpperCase(): Boolean = all { it.isUpperCase() }

fun inputAsPairs(input: List<String>): List<Pair<String, String>> =
    input.map { it.split('-') }.map { Pair(it[0], it[1]) }

fun buildCaveMap(pairs: List<Pair<String, String>>): CaveMap =
    (pairs.groupBy { it.first }.map { group -> group.key to group.value.map { it.second }.toSet() } +
            pairs.groupBy { it.second }.map { group -> group.key to group.value.map { it.first }.toSet() })
        .groupBy { it.first }.map { group -> group.key to group.value.fold(setOf<String>()) { a, (_, b) -> a + b } }
        .toMap()

tailrec fun allPathsThroughCaves(
    caveMap: CaveMap,
    pathQueue: MutableList<List<String>>,
    pathCount: Int,
    nextCaveCriterion: (String, List<String>) -> Boolean,
): Int =
    if (pathQueue.isEmpty())
        pathCount
    else if (pathQueue.first().last() == "end")
        allPathsThroughCaves(
            caveMap,
            pathQueue.apply { removeFirst() },
            pathCount + 1,
            nextCaveCriterion
        )
    else
        allPathsThroughCaves(
            caveMap,
            pathQueue.apply {
                addAll(caveMap[pathQueue.first().last()]!!.mapNotNull {
                    if (nextCaveCriterion(it, pathQueue.first()))
                        pathQueue.first() + it
                    else
                        null
                })
                removeFirst()
            },
            pathCount,
            nextCaveCriterion
        )

fun countPathsThroughCaves(
    caveMap: CaveMap,
    path: List<String>,
    nextCaveCriterion: (String, List<String>) -> Boolean,
): Int =
    if (path.last() == "end")
        1
    else
        caveMap[path.last()]!!.filter { nextCaveCriterion(it, path) }
            .sumOf { countPathsThroughCaves(caveMap, path + it, nextCaveCriterion) }

fun main() {
    fun part1(input: List<String>): Int =
        allPathsThroughCaves(
            buildCaveMap(inputAsPairs(input)),
            mutableListOf(listOf("start")),
            0
        ) { caveName, path -> caveName.isUpperCase() || !path.contains(caveName) }

    fun part2(input: List<String>): Int =
        allPathsThroughCaves(
            buildCaveMap(inputAsPairs(input)),
            mutableListOf(listOf("start")),
            0
        ) { caveName, path ->
            caveName != "start"
                    && (caveName.isUpperCase()
                    || !path.contains(caveName)
                    || path.groupingBy { it }.eachCount().none { !it.key.isUpperCase() && it.value >= 2 })
        }

    fun part1better(input: List<String>): Int =
        countPathsThroughCaves(
            buildCaveMap(inputAsPairs(input)),
            listOf("start"),
        ) { caveName, path -> caveName.isUpperCase() || !path.contains(caveName) }

    fun part2better(input: List<String>): Int =
        countPathsThroughCaves(
            buildCaveMap(inputAsPairs(input)),
            listOf("start"),
        ) { caveName, path ->
            caveName != "start"
                    && (caveName.isUpperCase()
                    || !path.contains(caveName)
                    || path.groupingBy { it }.eachCount().none { !it.key.isUpperCase() && it.value >= 2 })
        }

    val testInput1 = readInput("day12/Day12_test1")
    val testInput2 = readInput("day12/Day12_test2")
    val testInput3 = readInput("day12/Day12_test3")
    testAnswer(part1(testInput1), 10)
    testAnswer(part1(testInput2), 19)
    testAnswer(part1(testInput3), 226)
    testAnswer(part1better(testInput1), 10)
    testAnswer(part1better(testInput2), 19)
    testAnswer(part1better(testInput3), 226)
    testAnswer(part2(testInput1), 36)
    testAnswer(part2(testInput2), 103)
    testAnswer(part2(testInput3), 3509)
    testAnswer(part2better(testInput1), 36)
    testAnswer(part2better(testInput2), 103)
    testAnswer(part2better(testInput3), 3509)

    val input = readInput("day12/Day12")
    val wrongPart1Answers = listOf<Int>(
    )
    measureTimeMillis {
        println("Part 1: ${finalAnswerIsNotWrong(part1better(input), wrongPart1Answers)}")
    }.also { println("\ttook $it milliseconds") }

    val wrongPart2Answers = listOf<Int>(
    )
    measureTimeMillis {
        println("Part 2: ${finalAnswerIsNotWrong(part2better(input), wrongPart2Answers)}")
    }.also { println("\ttook $it milliseconds") }

    measureTimeMillis {
        println("Part 1: ${finalAnswerIsNotWrong(part1(input), wrongPart1Answers)}")
    }.also { println("\ttook $it milliseconds") }

    measureTimeMillis {
        println("Part 2: ${finalAnswerIsNotWrong(part2(input), wrongPart2Answers)}")
    }.also { println("\ttook $it milliseconds") }
}
