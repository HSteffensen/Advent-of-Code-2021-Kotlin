package day18

import finalAnswerIsNotWrong
import readInput
import testAnswer
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

interface SnailNumber {
    val magnitude: Int
    fun shouldExplode(depth: Int = 0): Boolean
    fun shouldSplit(): Boolean
    fun splitHelper(): Pair<SnailNumber, Boolean>
    fun explodeHelperPassLeftValue(value: Int): SnailNumber
    fun explodeHelperPassRightValue(value: Int): SnailNumber

    operator fun plus(other: SnailNumber) =
        ofPair(this, other)

    companion object {
        fun ofPair(left: SnailNumber, right: SnailNumber) = SnailNumberPair(left, right)
        fun ofValue(value: Int) = SnailNumberValue(value)
    }
}

class SnailNumberValue(val value: Int) : SnailNumber {
    override val magnitude: Int = value
    override fun shouldExplode(depth: Int): Boolean = false

    override fun shouldSplit(): Boolean = value >= 10

    override fun splitHelper(): Pair<SnailNumber, Boolean> =
        Pair(
            if (shouldSplit())
                SnailNumber.ofPair(
                    SnailNumber.ofValue(value / 2),
                    SnailNumber.ofValue((value / 2) + (value % 2))
                )
            else
                this,
            shouldSplit()
        )

    override fun explodeHelperPassLeftValue(value: Int): SnailNumber =
        SnailNumber.ofValue(this.value + value)

    override fun explodeHelperPassRightValue(value: Int): SnailNumber =
        SnailNumber.ofValue(this.value + value)

    override fun toString(): String = value.toString()
}

class SnailNumberPair(val left: SnailNumber, val right: SnailNumber) : SnailNumber {
    override val magnitude: Int = (3 * left.magnitude) + (2 * right.magnitude)
    override fun shouldExplode(depth: Int): Boolean =
        if (depth == 4)
            true
        else
            left.shouldExplode(depth + 1) || right.shouldExplode(depth + 1)

    override fun shouldSplit(): Boolean =
        left.shouldSplit() || right.shouldSplit()


    private fun explodeHelper(depth: Int): Pair<SnailNumber, Pair<Int, Int>?> =
        if (depth >= 4
            && left is SnailNumberValue && right is SnailNumberValue)
            Pair(SnailNumber.ofValue(0), Pair(left.value, right.value))
        else
            when (left) {
                is SnailNumberValue -> Pair(left, null)
                is SnailNumberPair -> left.explodeHelper(depth + 1)
                else -> throw IllegalStateException("There should be only two types of SnailNumber.")
            }.let { (newLeft, leftExplodeResult) ->
                if (leftExplodeResult != null)
                    if (leftExplodeResult.second != 0)
                        Pair(
                            SnailNumber.ofPair(newLeft, right.explodeHelperPassRightValue(leftExplodeResult.second)),
                            Pair(leftExplodeResult.first, 0)
                        )
                    else
                        Pair(SnailNumber.ofPair(newLeft, right), leftExplodeResult)
                else
                    when (right) {
                        is SnailNumberValue -> Pair(right, null)
                        is SnailNumberPair -> right.explodeHelper(depth + 1)
                        else -> throw IllegalStateException("There should be only two types of SnailNumber.")
                    }.let { (newRight, rightExplodeResult) ->
                        if (rightExplodeResult != null)
                            if (rightExplodeResult.first != 0)
                                Pair(
                                    SnailNumber.ofPair(newLeft.explodeHelperPassLeftValue(rightExplodeResult.first), newRight),
                                    Pair(0, rightExplodeResult.second)
                                )
                            else
                                Pair(SnailNumber.ofPair(newLeft, newRight), rightExplodeResult)
                        else
                            Pair(SnailNumber.ofPair(newLeft, newRight), null)
                    }
            }

    fun explode(): SnailNumberPair =
        explodeHelper(0).first.let {
            if (it is SnailNumberPair)
                it
            else
                throw IllegalStateException("how bizarre")
        }

    override fun splitHelper(): Pair<SnailNumber, Boolean> =
        left.splitHelper().let { (newLeft, hasSplit) ->
            if (hasSplit)
                Pair(SnailNumber.ofPair(newLeft, right), true)
            else
                right.splitHelper().let { (newRight, rightHasSplit) ->
                    if (rightHasSplit)
                        Pair(SnailNumber.ofPair(newLeft, newRight), true)
                    else
                        Pair(SnailNumber.ofPair(newLeft, newRight), false)
                }
        }

    fun split(): SnailNumberPair =
        splitHelper().first.let {
            if (it is SnailNumberPair)
                it
            else
                throw IllegalStateException("how bizarre")
        }

    override fun explodeHelperPassLeftValue(value: Int): SnailNumber =
        SnailNumber.ofPair(left, right.explodeHelperPassLeftValue(value))

    override fun explodeHelperPassRightValue(value: Int): SnailNumber =
        SnailNumber.ofPair(left.explodeHelperPassRightValue(value), right)

    override fun toString(): String =
        "[${left},${right}]"

    fun reduce(): SnailNumberPair =
        reduceSnailNumber(this)
}

tailrec fun reduceSnailNumber(number: SnailNumberPair): SnailNumberPair =
    if (number.shouldExplode())
        reduceSnailNumber(number.explode())
    else if (number.shouldSplit())
        reduceSnailNumber(number.split())
    else
        number

private fun readSnailNumberHelper(line: String): Pair<SnailNumber, String> =
    if (line.first() == '[')
        readSnailNumberHelper(line.drop(1))
            .let { (number, remaining) ->
                if (remaining.first() != ',')
                    throw IllegalStateException("Expected ',' at the front: '$remaining'")
                else
                    readSnailNumberHelper(remaining.drop(1))
                        .let { (number2, remaining2) ->
                            if (remaining2.first() != ']')
                                throw IllegalStateException("Expected ']' at the front: '$remaining'")
                            else
                                Pair(
                                    SnailNumber.ofPair(number, number2),
                                    remaining2.drop(1)
                                )
                        }
            }
    else if (line.first().isDigit())
        Pair(
            SnailNumber.ofValue(line.first().digitToInt()),
            line.drop(1)
        )
    else
        throw IllegalStateException("Expected '[' or a digit at the front: '$line'")

fun readSnailNumber(line: String): SnailNumberPair =
    readSnailNumberHelper(line).let { (number, remainingString) ->
        if (remainingString.isNotEmpty())
            throw IllegalStateException("There shouldn't be leftover string in readSnailNumber: '$remainingString'")
        else
            number.let {
                if (it is SnailNumberPair)
                    it
                else
                    throw IllegalStateException("how bizarre")
            }
    }

fun parseInput(input: List<String>): List<SnailNumber> =
    input.map { readSnailNumber(it) }

fun main() {
    fun part1(input: List<String>): Int =
        parseInput(input).reduce { a, b -> (a + b).reduce() }.magnitude

    fun part2(input: List<String>): Int =
        parseInput(input).let { numbers ->
            numbers.flatMapIndexed { index, number -> numbers.filterIndexed { i, _ -> i != index }.map { number + it } }
        }.maxOf { it.reduce().magnitude }

    val testInputRead = readInput("day18/Day18_test_read")
    parseInput(testInputRead).zip(testInputRead).forEach { (number, input) ->
        testAnswer(number.toString(), input)
    }.also { println("Test parseInput passed") }
    testAnswer(readSnailNumber("[[[[[9,8],1],2],3],4]").explode().toString(), "[[[[0,9],2],3],4]")
    testAnswer(readSnailNumber("[7,[6,[5,[4,[3,2]]]]]").explode().toString(), "[7,[6,[5,[7,0]]]]")
    testAnswer(readSnailNumber("[[6,[5,[4,[3,2]]]],1]").explode().toString(), "[[6,[5,[7,0]]],3]")
    testAnswer(readSnailNumber("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").explode().toString(),
        "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
    testAnswer(readSnailNumber("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]").explode().toString(),
        "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")
    println("explode passed")
    val testPlusReduceNumber = readSnailNumber("[[[[4,3],4],4],[7,[[8,4],9]]]") + readSnailNumber("[1,1]")
    testAnswer(
        testPlusReduceNumber.shouldExplode(),
        true
    )
    testAnswer(
        testPlusReduceNumber.explode().toString(),
        "[[[[0,7],4],[7,[[8,4],9]]],[1,1]]"
    )
    testAnswer(
        testPlusReduceNumber.explode().shouldExplode(),
        true
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().toString(),
        "[[[[0,7],4],[15,[0,13]]],[1,1]]"
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().shouldExplode(),
        false
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().shouldSplit(),
        true
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().toString(),
        "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().shouldExplode(),
        false
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().shouldSplit(),
        true
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().split().toString(),
        "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().split().shouldExplode(),
        true
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().split().explode().toString(),
        "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().split().explode().shouldExplode(),
        false
    )
    testAnswer(
        testPlusReduceNumber.explode().explode().split().split().explode().shouldSplit(),
        false
    )
    testAnswer(
        testPlusReduceNumber.reduce().toString(),
        "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"
    )
    println("reduce passed")

    val testInput1 = readInput("day18/Day18_test1")
    testAnswer(part1(testInput1), 4140).also { println("Test part 1 passed") }
    testAnswer(part2(testInput1), 3993).also { println("Test part 2 passed") }

    val input = readInput("day18/Day18")
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
