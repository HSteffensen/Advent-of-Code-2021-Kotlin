package day21

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun d100Increasing() = generateSequence(1) { (it % 100) + 1 }

val quantumD3 = listOf(
    listOf(1, 1, 1),
    listOf(1, 1, 2),
    listOf(1, 1, 3),
    listOf(1, 2, 1),
    listOf(1, 2, 2),
    listOf(1, 2, 3),
    listOf(1, 3, 1),
    listOf(1, 3, 2),
    listOf(1, 3, 3),
    listOf(2, 1, 1),
    listOf(2, 1, 2),
    listOf(2, 1, 3),
    listOf(2, 2, 1),
    listOf(2, 2, 2),
    listOf(2, 2, 3),
    listOf(2, 3, 1),
    listOf(2, 3, 2),
    listOf(2, 3, 3),
    listOf(3, 1, 1),
    listOf(3, 1, 2),
    listOf(3, 1, 3),
    listOf(3, 2, 1),
    listOf(3, 2, 2),
    listOf(3, 2, 3),
    listOf(3, 3, 1),
    listOf(3, 3, 2),
    listOf(3, 3, 3),
).groupingBy { it.sum() }.eachCount().toList()

class Die(private var generator: Sequence<Int>) {
    fun roll3(): List<Int> = generator.take(3).toList().also { generator = generator.drop(3) }
}

data class GameState(
    val p1Pos: Int,
    val p2Pos: Int,
    val p1Score: Int = 0,
    val p2Score: Int = 0,
    val timesDieRolled: Int = 0
) {
    fun nextState(player1Turn: Boolean, die: Die): GameState =
        nextState(player1Turn, die.roll3().sum())

    fun nextState(player1Turn: Boolean, dieRoll: Int): GameState =
        if (player1Turn)
            (((p1Pos + dieRoll - 1) % 10) + 1).let { newP1Pos ->
                GameState(
                    newP1Pos,
                    p2Pos,
                    p1Score + newP1Pos,
                    p2Score,
                    timesDieRolled + 3
                )
            }
        else
            (((p2Pos + dieRoll - 1) % 10) + 1).let { newP2Pos ->
                GameState(
                    p1Pos,
                    newP2Pos,
                    p1Score,
                    p2Score + newP2Pos,
                    timesDieRolled + 3
                )
            }
}

fun parseInput(input: List<String>): Pair<Int, Int> =
    input.take(2).map { it.split(' ').last().toInt() }.let { Pair(it[0], it[1]) }

tailrec fun runGame(gameState: GameState, die: Die, player1Turn: Boolean = true): GameState =
    if (max(gameState.p1Score, gameState.p2Score) >= 1000)
        gameState
    else
        runGame(gameState.nextState(player1Turn, die), die, !player1Turn)

fun runQuantumGame(gameState: GameState, player1Turn: Boolean = true): Pair<Long, Long> =
    if (gameState.p1Score >= 21)
        Pair(1, 0)
    else if (gameState.p2Score >= 21)
        Pair(0, 1)
    else
        quantumD3.fold(Pair(0, 0)) { (totalP1Wins, totalP2Wins), (rollResult, universesWithThisResult) ->
            runQuantumGame(gameState.nextState(player1Turn, rollResult), !player1Turn)
                .let { (p1Wins, p2Wins) ->
                    Pair(
                        totalP1Wins + (p1Wins * universesWithThisResult),
                        totalP2Wins + (p2Wins * universesWithThisResult)
                    )
                }
        }

fun main() {
    fun part1(input: List<String>): Int =
        parseInput(input).let { (p1Pos, p2Pos) ->
            runGame(GameState(p1Pos, p2Pos), Die(d100Increasing()))
        }.let { (_, _, p1Score, p2Score, timesDieRolled) ->
            min(p1Score, p2Score) * timesDieRolled
        }

    fun part2(input: List<String>): Long =
        parseInput(input).let { (p1Pos, p2Pos) ->
            runQuantumGame(GameState(p1Pos, p2Pos))
        }.let { (p1Wins, p2Wins) -> max(p1Wins, p2Wins) }

    val testD100Increasing = Die(d100Increasing())
    testAnswer(testD100Increasing.roll3().sum(), (1..3).sum()).also { println("Test Die.roll3 passed") }
    testAnswer(testD100Increasing.roll3().sum(), (4..6).sum()).also { println("Test Die.roll3 passed") }

    val testInput = readInput("day21/test")
    testAnswer(part1(testInput), 739785).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 444356092776315).also { println("Test part 2 passed") }

    val input = readInput("day21/input")
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
