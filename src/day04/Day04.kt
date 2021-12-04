package day04

import finalAnswerIsNotWrong
import readInput
import testAnswer

inline fun <reified T> checkBingo(board: List<List<T>>, isMarked: (T) -> Boolean): Boolean =
    (board + board.indices.map { row -> board.indices.map { column -> board[column][row] } })
        .fold(false) {resultSoFar, row -> resultSoFar || row.fold(true) {a, b -> a && isMarked(b)} }

inline fun <reified T> winningBingoLines(board: List<List<T>>, isMarked: (T) -> Boolean): List<List<T>> =
    (board + board.indices.map { row -> board.indices.map { column -> board[column][row] } })
        .fold(listOf()) { resultSoFar, row -> resultSoFar + if (row.fold(true) { a, b -> a && isMarked(b)}) listOf(row) else listOf() }

inline fun <reified T> countBingoWinningLines(board: List<List<T>>, isMarked: (T) -> Boolean): Int =
    winningBingoLines(board, isMarked).size


fun main() {
    fun boardString(board: List<List<Pair<Int, Boolean>>>): String =
        board.map{ row -> row.map { "${it.first}${if (it.second) "T" else "F"}" }.joinToString(" ") }.joinToString("\n")

    fun part1(input: List<String>): Int =
        input.partition { it.length > 20 }
            .let { (numbers, boards) -> Pair(
                numbers.first().split(",").map { it.toInt() },
                boards.filter { it.isNotEmpty() }.map { board -> board.trim().split(Regex(" +")).map { Pair(it.toInt(), false) } }.chunked(5)
            ) }
            .let { (numbers, initialBoards) ->
                numbers.fold(Pair(initialBoards, -1)) { (boards, value), number ->
                    if (value == -1)
                        boards.map { board -> board.map { row -> row.map { if (it.first == number) Pair(it.first, true) else it } } }
                            .let { nextBoards ->
                                if (nextBoards.fold(false) { a, b -> a || checkBingo(b) { it.second } })
                                    Pair(nextBoards, number)
                                else
                                    Pair(nextBoards, -1)
                            }
                    else
                        Pair(boards, value)
                }
            }
            .let { (boards, value) ->
                boards.first { board -> checkBingo(board) { it.second } }.flatten().filter { !it.second }.sumOf { it.first } * value
            }

    fun part2(input: List<String>): Int =
        input.partition { it.length > 20 }
            .let { (numbers, boards) -> Pair(
                numbers.first().split(",").map { it.toInt() },
                boards.filter { it.isNotEmpty() }.map { board -> board.trim().split(Regex(" +")).map { Pair(it.toInt(), false) } }.chunked(5)
            ) }
            .let { (numbers, initialBoards) ->
                numbers.fold(Pair(initialBoards, -1)) { (boards, value), number ->
                    if (value == -1)
                        boards.map { board -> board.map { row -> row.map { if (it.first == number) Pair(it.first, true) else it } } }
                            .let { nextBoards ->
                                if (nextBoards.fold(true) { a, b -> a && checkBingo(b) { it.second } })
                                    Pair(nextBoards, number)
                                else
                                    Pair(nextBoards, -1)
                            }
                    else
                        Pair(boards, value)
                }
            }
            .let { (boards, value) ->
                boards.filter { board -> winningBingoLines(board) { it.second }.let { wins -> wins.size == 1 && wins.first().any { it.first == value } } }
                    .also { check(it.size == 1) }.first()
                    .flatten().filter { !it.second }
                    .sumOf { it.first } * value
            }



    check(checkBingo(listOf(listOf(1))) { it > 0 })
    check(!checkBingo(listOf(listOf(0))) { it > 0 })
    check(checkBingo(listOf(listOf(1,0),listOf(1,0))) { it > 0 })
    check(checkBingo(listOf(listOf(1,1),listOf(0,0))) { it > 0 })
    check(checkBingo(listOf(listOf(0,0),listOf(1,1))) { it > 0 })
    check(checkBingo(listOf(listOf(0,1),listOf(0,1))) { it > 0 })
    check(!checkBingo(listOf(listOf(0,0),listOf(0,0))) { it > 0 })
    check(!checkBingo(listOf(listOf(1,0),listOf(0,1))) { it > 0 })
    check(!checkBingo(listOf(listOf(0,1),listOf(1,0))) { it > 0 })
    check(countBingoWinningLines(listOf(listOf(1))) { it > 0 } == 2)
    check(countBingoWinningLines(listOf(listOf(0))) { it > 0 } == 0)
    check(countBingoWinningLines(listOf(listOf(1,0),listOf(1,0))) { it > 0 } == 1)
    check(countBingoWinningLines(listOf(listOf(1,1),listOf(0,0))) { it > 0 } == 1)
    check(countBingoWinningLines(listOf(listOf(0,0),listOf(1,1))) { it > 0 } == 1)
    check(countBingoWinningLines(listOf(listOf(0,1),listOf(0,1))) { it > 0 } == 1)
    check(countBingoWinningLines(listOf(listOf(1,1),listOf(1,0))) { it > 0 } == 2)
    check(countBingoWinningLines(listOf(listOf(1,1),listOf(0,1))) { it > 0 } == 2)
    check(countBingoWinningLines(listOf(listOf(1,0),listOf(1,1))) { it > 0 } == 2)
    check(countBingoWinningLines(listOf(listOf(0,1),listOf(1,1))) { it > 0 } == 2)
    check(countBingoWinningLines(listOf(listOf(1,1),listOf(1,1))) { it > 0 } == 4)
    check(countBingoWinningLines(listOf(listOf(0,0),listOf(0,0))) { it > 0 } == 0)
    check(countBingoWinningLines(listOf(listOf(1,0),listOf(0,1))) { it > 0 } == 0)
    check(countBingoWinningLines(listOf(listOf(0,1),listOf(1,0))) { it > 0 } == 0)

    val testInput = readInput("day04/Day04_test")
    testAnswer(part1(testInput), 4512)
    testAnswer(part2(testInput), 1924)

    val input = readInput("day04/Day04")
    val wrongPart1Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part1(input), wrongPart1Answers))

    val wrongPart2Answers = listOf<Int>(
    )
    println(finalAnswerIsNotWrong(part2(input), wrongPart2Answers))
}
