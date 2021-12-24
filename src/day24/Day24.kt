package day24

import finalAnswerIsNotWrong
import readInput
import testAnswer
import java.lang.IllegalArgumentException
import kotlin.system.measureTimeMillis

private fun runMathInstruction(instructionName: String, value1: Long, value2: Long): Long =
    when (instructionName) {
        "add" -> value1 + value2
        "mul" -> value1 * value2
        "div" -> value1 / value2
        "mod" -> value1 % value2
        "eql" -> if (value1 == value2) 1 else 0
        else -> throw IllegalArgumentException("Illegal instructionName in runMathInstruction: $instructionName")
    }

fun runAluProgram(programLines: List<String>, inputsIterator: Iterator<Long>): Map<Char, Long> {
    val registers: MutableMap<Char, Long> = mutableMapOf(
        'w' to 0,
        'x' to 0,
        'y' to 0,
        'z' to 0,
    )
    programLines.forEach { line ->
        if (line.startsWith("inp ")) {
            registers[line.split(' ')[1].single()] = inputsIterator.next()
        } else {
            registers[line[4]] = runMathInstruction(
                line.substring(0..2),
                registers[line[4]]!!,
                registers.getOrDefault(line[6], line.substring(6, line.length).toLongOrNull())!!
                )
        }
    }
    return registers
}

fun modelNumberValidationCode(aluCode: List<String>, modelNumber: Iterator<Long>): Long =
    runAluProgram(aluCode, modelNumber)['z']!!

class MonadSimulator {
    val addXValues = listOf(12, 11, 13, 11, 14, -10, 11, -9, -3, 13, -5, -10, -4, -5)
    val divZValues = listOf(1, 1, 1, 1, 1, 26, 1, 26, 26, 1, 26, 26, 26, 26)
    val addYValues = listOf(4, 11, 5, 11, 14, 7, 11, 4, 6, 5, 9, 12, 14, 14)

    fun simulateMonadStep(
        step: Int, // 0 to 13
        z: Long,
        w: Int
    ): Long =
        if (z % 26 + addXValues[step] - w != 0L)
            ((z / divZValues[step]) * 26) + w + addYValues[step]
        else
            z / divZValues[step]
}
val monadSimulator = MonadSimulator()

fun simulateMonad(input: List<Int>): Long =
    input.foldIndexed(0) { step, z, w ->
        monadSimulator.simulateMonadStep(step, z, w)
    }

fun simulateMonadFindHighestSuccess(step: Int, z: Long, prefix: Long, visited: MutableSet<Pair<Int, Long>>): Long? =
    if (step == 14)
        if (z == 0L)
            prefix
        else
            null
    else if (visited.contains(Pair(step, z)))
        null
    else
        (9 downTo 0).asSequence().map { w ->
            simulateMonadFindHighestSuccess(
                step + 1,
                monadSimulator.simulateMonadStep(step, z, w),
                prefix * 10 + w,
                visited
            )
        }.firstOrNull { it != null }.also { visited.add(Pair(step, z)) }

fun simulateMonadFindHighestSuccess(): Long =
    simulateMonadFindHighestSuccess(0, 0, 0, mutableSetOf())!!

fun simulateMonadFindLowestSuccess(step: Int, z: Long, prefix: Long, visited: MutableSet<Pair<Int, Long>>): Long? =
    if (step == 14)
        if (z == 0L)
            prefix
        else
            null
    else if (visited.contains(Pair(step, z)))
        null
    else
        (1..9).asSequence().map { w ->
            simulateMonadFindLowestSuccess(
                step + 1,
                monadSimulator.simulateMonadStep(step, z, w),
                prefix * 10 + w,
                visited
            )
        }.firstOrNull { it != null }.also { visited.add(Pair(step, z)) }

fun simulateMonadFindLowestSuccess(): Long =
    simulateMonadFindLowestSuccess(0, 0, 0, mutableSetOf())!!

fun main() {
    fun part1(input: List<String>): Long =
        simulateMonadFindHighestSuccess()

    fun part2(input: List<String>): Long =
        simulateMonadFindLowestSuccess()

//    val testInput = readInput("day24/test")
//    testAnswer(part1(testInput), 0).also { println("Test part 1 passed") }
//    testAnswer(part2(testInput), 0).also { println("Test part 2 passed") }

    val input = readInput("day24/input")
//    println(
//        modelNumberValidationCode(input,
//        "92345678912345".map { it.digitToInt().toLong() }.iterator())
//    )
//    println(simulateMonad("92345678912345".map { it.digitToInt() }))
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
