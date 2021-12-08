package day08


import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.system.measureTimeMillis


fun main() {
    fun String.overlapCount(other: String): Int {
        return this.count { other.contains(it) }
    }

    fun List<Pair<Int, String>>.getLettersOf(value: Int): String {
        return this.first { it.first == value }.second
    }

    fun List<Pair<Int, String>>.getValueOf(letters: String): Int {
        return this.first { it.second.toSortedSet() == letters.toSortedSet() }.first
    }

    fun part1(input: List<String>): Int =
        input.map { it.split('|')[1].trim().split(' ') }
            .sumOf { numbers -> numbers.count { it.length != 5 && it.length != 6 } }

    fun part2(input: List<String>): Int =
        input.map { line -> line.split('|').map { it.trim().split(' ') } }.map { Pair(it[0], it[1]) }
            .map { (symbols, resultSymbols) ->
                symbols.map { Pair(-1, it) }
                    .map { (value, letters) ->
                        Pair(
                            when (letters.length) {
                                2 -> 1
                                3 -> 7
                                4 -> 4
                                7 -> 8
                                else -> value
                            }, letters
                        )
                    }
                    .let { solvingSymbols ->
                        solvingSymbols.map { (value, letters) ->
                            Pair(
                                when (letters.length) {
                                    5 -> if (letters.overlapCount(solvingSymbols.getLettersOf(1)) == 2) 3
                                    else if (letters.overlapCount(solvingSymbols.getLettersOf(4)) == 2) 2
                                    else 5
                                    6 -> if (letters.overlapCount(solvingSymbols.getLettersOf(1)) == 1) 6
                                    else if (letters.overlapCount(solvingSymbols.getLettersOf(4)) == 4) 9
                                    else 0
                                    else -> value
                                }, letters
                            )
                        }
                    }
                    .let { solvedSymbols ->
                        resultSymbols.map { solvedSymbols.getValueOf(it) }
                    }
            }
            .sumOf { it.reduce { a, b -> a * 10 + b } }

    val testInput = readInput("day08/Day08_test")
    testAnswer(part1(testInput), 26)
    testAnswer(part2(testInput), 61229)

    val input = readInput("day08/Day08")
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
