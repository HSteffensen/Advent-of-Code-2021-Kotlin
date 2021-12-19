package day19

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.abs
import kotlin.system.measureTimeMillis

typealias Position3 = Triple<Int,Int,Int>
typealias PointCloud = Set<Position3>

enum class RotationAxis {
    X,
    Y,
    Z
}
enum class FacingAxis {
    X,
    Y,
    Z,
    NegX,
    NegY,
    NegZ
}
enum class HeadUpAxis {
    // which way is N isn't actually important, just need to differentiate the 4 options
    N,
    E,
    S,
    W
}
typealias TrueHeading = Pair<FacingAxis, HeadUpAxis>
typealias Rotation = Pair<RotationAxis, Int>
val allFacings: Map<TrueHeading, List<Rotation>> = mapOf(
    // start at facing Z, head is N
    TrueHeading(FacingAxis.Z, HeadUpAxis.N) to listOf(),
    TrueHeading(FacingAxis.Z, HeadUpAxis.E) to listOf(RotationAxis.Z to 1),
    TrueHeading(FacingAxis.Z, HeadUpAxis.S) to listOf(RotationAxis.Z to 2),
    TrueHeading(FacingAxis.Z, HeadUpAxis.W) to listOf(RotationAxis.Z to 3),
    TrueHeading(FacingAxis.Y, HeadUpAxis.N) to listOf(RotationAxis.X to 3),
    TrueHeading(FacingAxis.Y, HeadUpAxis.E) to listOf(RotationAxis.X to 3, RotationAxis.Y to 1),
    TrueHeading(FacingAxis.Y, HeadUpAxis.S) to listOf(RotationAxis.X to 3, RotationAxis.Y to 2),
    TrueHeading(FacingAxis.Y, HeadUpAxis.W) to listOf(RotationAxis.X to 3, RotationAxis.Y to 3),
    TrueHeading(FacingAxis.X, HeadUpAxis.N) to listOf(RotationAxis.Y to 1),
    TrueHeading(FacingAxis.X, HeadUpAxis.E) to listOf(RotationAxis.Y to 1, RotationAxis.X to 1),
    TrueHeading(FacingAxis.X, HeadUpAxis.S) to listOf(RotationAxis.Y to 1, RotationAxis.X to 2),
    TrueHeading(FacingAxis.X, HeadUpAxis.W) to listOf(RotationAxis.Y to 1, RotationAxis.X to 3),
    TrueHeading(FacingAxis.NegZ, HeadUpAxis.N) to listOf(RotationAxis.Y to 2),
    TrueHeading(FacingAxis.NegZ, HeadUpAxis.E) to listOf(RotationAxis.Y to 2, RotationAxis.Z to 1),
    TrueHeading(FacingAxis.NegZ, HeadUpAxis.S) to listOf(RotationAxis.Y to 2, RotationAxis.Z to 2),
    TrueHeading(FacingAxis.NegZ, HeadUpAxis.W) to listOf(RotationAxis.Y to 2, RotationAxis.Z to 3),
    TrueHeading(FacingAxis.NegY, HeadUpAxis.N) to listOf(RotationAxis.X to 1),
    TrueHeading(FacingAxis.NegY, HeadUpAxis.E) to listOf(RotationAxis.X to 1, RotationAxis.Y to 1),
    TrueHeading(FacingAxis.NegY, HeadUpAxis.S) to listOf(RotationAxis.X to 1, RotationAxis.Y to 2),
    TrueHeading(FacingAxis.NegY, HeadUpAxis.W) to listOf(RotationAxis.X to 1, RotationAxis.Y to 3),
    TrueHeading(FacingAxis.NegX, HeadUpAxis.N) to listOf(RotationAxis.Y to 3),
    TrueHeading(FacingAxis.NegX, HeadUpAxis.E) to listOf(RotationAxis.Y to 3, RotationAxis.X to 1),
    TrueHeading(FacingAxis.NegX, HeadUpAxis.S) to listOf(RotationAxis.Y to 3, RotationAxis.X to 2),
    TrueHeading(FacingAxis.NegX, HeadUpAxis.W) to listOf(RotationAxis.Y to 3, RotationAxis.X to 3),
)

operator fun Position3.minus(other: Position3) =
    Triple(
        this.first - other.first,
        this.second - other.second,
        this.third - other.third,
    )

operator fun Position3.plus(other: Position3) =
    Triple(
        this.first + other.first,
        this.second + other.second,
        this.third + other.third,
    )

fun Position3.magnitude(): Int =
    toList().reduce { a, b -> abs(a) + abs(b) }

fun Position3.manhattanDistance(other: Position3): Int =
    (other - this).magnitude()

fun Position3.rotate(axis: RotationAxis, times: Int = 1): Position3 =
    if (times % 4 == 0)
        this
    else
        when (axis) {
            RotationAxis.X -> Position3(first, third, -second)
            RotationAxis.Y -> Position3(-third, second, first)
            RotationAxis.Z -> Position3(second, -first, third)
        }.rotate(axis, times - 1)

fun Position3.rotations(rotationList: List<Rotation>): Position3 =
    rotationList.fold(this) { p, r -> p.rotate(r.first, r.second) }

fun PointCloud.faceToward(heading: TrueHeading): PointCloud =
    map { it.rotations(allFacings[heading]!!) }.toSet()

fun PointCloud.eachFacing(): List<Pair<TrueHeading, PointCloud>> =
    allFacings.keys.map { it to this.faceToward(it) }

fun PointCloud.mostCommonDifference(other: PointCloud): Pair<Position3, Int> =
    flatMap { pos -> other.map { pos - it } }
        .groupingBy { it }.eachCount()
        .map { Pair(it.key, it.value) }
        .maxByOrNull { it.second }!!

typealias RelativePosition = Pair<Position3, TrueHeading>
fun PointCloud.getRelativePosition(other: PointCloud, threshold: Int = 12): RelativePosition? =
    other.eachFacing().map { it.first to mostCommonDifference(it.second) }
        .maxByOrNull { (_, difference) -> difference.second }
        ?.let { (heading, posDifference) ->
            if (posDifference.second >= threshold)
                posDifference.first to heading
            else
                null
        }

fun PointCloud.canAlignWith(other: PointCloud, threshold: Int = 12): Boolean =
    other.eachFacing().map { mostCommonDifference(it.second) }
        .any { it.second >= threshold }

fun PointCloud.alignWith(other: PointCloud, threshold: Int = 12): PointCloud? =
    other.eachFacing().map { it to mostCommonDifference(it.second) }
        .maxByOrNull { (_, difference) -> difference.second }
        ?.let { (alignedOther, difference) ->
            if (difference.second >= threshold)
                alignedOther.second.map { it + difference.first }.toSet() + this
            else
                null
        }

fun parseScannerInput(input: List<String>): PointCloud =
    input.drop(1)
        .map { line -> line.split(',').map { it.toInt() } }
        .map { Triple(it[0],it[1],it[2]) }.toSet()

fun parseInput(input: List<String>): List<PointCloud> =
    listOf(parseScannerInput(input.takeWhile { it != "" })) +
            if (input.contains(""))
                parseInput(input.drop(input.indexOf("")+1))
            else
                listOf()

fun mergePointClouds(pointClouds: List<PointCloud>): Pair<PointCloud, Map<Int, Position3>> =
    mergePointClouds(
        pointClouds.mapIndexed { index, it -> index to it }.toMap(),
        mapOf(0 to Position3(0,0,0))
    )

fun mergePointClouds(pointClouds: Map<Int,PointCloud>, scannerPositions: Map<Int, Position3>): Pair<PointCloud, Map<Int, Position3>> =
    if (pointClouds.size == 1)
        pointClouds[0]!! to scannerPositions
    else
        pointClouds.filterKeys { it != 0 }.toList()
            .fold(Triple(pointClouds[0]!!, mapOf<Int, PointCloud>(), mapOf<Int, Position3>()))
            { (cloud, otherClouds, relPosMap), (index, newCloud) ->
                if (cloud.canAlignWith(newCloud))
                    Triple(cloud.alignWith(newCloud)!!, otherClouds, relPosMap + (index to cloud.getRelativePosition(newCloud)!!.first))
                else
                    Triple(cloud, otherClouds + mapOf(index to newCloud), relPosMap)
            }
            .let { (cloud, otherClouds, relPosMap) ->
                mergePointClouds(
                    mapOf(0 to cloud) + otherClouds,
                    relPosMap + scannerPositions
                )
            }

fun main() {
    fun part1(input: List<String>): Int =
        mergePointClouds(parseInput(input)).first.size

    fun part2(input: List<String>): Int =
        mergePointClouds(parseInput(input)).second
            .map { it.value }
            .let { scannerPosList ->
                scannerPosList.flatMap { scannerPos ->
                    scannerPosList.map { scannerPos.manhattanDistance(it) }
                }
            }
            .maxOf { it }

    val testInput = readInput("day19/Day19_test")
    testAnswer(part1(testInput), 79).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 3621).also { println("Test part 2 passed") }

    val input = readInput("day19/Day19")
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
