package day15

import SquareGrid
import finalAnswerIsNotWrong
import readInput
import testAnswer
import java.util.*
import kotlin.system.measureTimeMillis

fun parseInput(input: List<String>): SquareGrid<Int> =
    SquareGrid.fromListOfLists(
        input.map { line ->
            line.map { it.digitToInt() }
        }
    )

fun expandInputGridPosition(
    position: Pair<Int, Int>,
    value: Int,
    maxX: Int,
    maxY: Int
): List<Pair<Pair<Int, Int>, Int>> =
    (0 until 5).flatMap { x ->
        (0 until 5).map { y ->
            Pair(
                Pair(position.first + ((maxX + 1) * x), position.second + ((maxY + 1) * y)),
                ((value + x + y) % (10)) + ((value + x + y) / (10))
            )
        }
    }

fun parseInputPart2(input: List<String>): SquareGrid<Int> =
    SquareGrid.fromListOfPairs(
        parseInput(input).toList().let { smallGrid ->
            Pair(smallGrid.maxOf { it.first.first }, smallGrid.maxOf { it.first.second }).let { (maxX, maxY) ->
                smallGrid.flatMap { (position, value) ->
                    expandInputGridPosition(position, value, maxX, maxY)
                }
            }
        }
    )

fun Pair<Int, Int>.neighbors(): List<Pair<Int, Int>> =
    listOf(Pair(-1, 0), Pair(0, -1), Pair(1, 0), Pair(0, 1)).map { (dx, dy) -> Pair(first + dx, second + dy) }

inline fun <reified T> SquareGrid<T>.toGridString() =
    this.toList().let { gridList ->
        (0..gridList.maxOf { it.first.first }).map { x ->
            (0..gridList.maxOf { it.first.second }).map { y ->
                if (this[x, y] == null)
                    '.'
                else
                    this[x, y].toString()
            }.joinToString()
        }.joinToString("\n")
    } + "\n"

//tailrec fun findShortestPath(
//    grid: SquareGrid<Int>,
//    searchGrid: SquareGrid<Int?>,
//    targetPosition: Pair<Int, Int>,
//    searchValue: Int = 0
//): Int =
//    if (searchGrid[targetPosition] == searchValue)
//        searchValue
//    else
//        findShortestPath(
//            grid,
//            SquareGrid.fromListOfPairs(searchGrid.toList().map { gridItem ->
//                if (gridItem.second != null)
//                    gridItem
//                else
//                    Pair(
//                        gridItem.first,
//                        if (gridItem.first == Pair(0, 0)) 0 else
//                            gridItem.first.neighbors().mapNotNull { searchGrid[it] }
//                                .let { neighbors ->
//                                    if (neighbors.isEmpty()) null else
//                                        (neighbors.minOf { it } + grid[gridItem.first]!!)
//                                            .let { if (it > searchValue + 1) null else it }
//                                }
//                    )
//            }),
//            targetPosition,
//            (searchValue + 1).also { println(it) }
//        )

tailrec fun findShortestPath(
    grid: SquareGrid<Int>,
    searchGrid: Map<Pair<Int, Int>, Int>,
    targetPosition: Pair<Int, Int>,
    searchValue: Int = 0
): Int =
    if (searchGrid[targetPosition] == searchValue - 1)
        searchValue - 1
    else
        findShortestPath(
            grid,
            searchGrid +
                    searchGrid.flatMap { it.key.neighbors() }.toSet()
                        .filter { grid.containsKey(it) }
                        .mapNotNull { position ->
                            if (position.neighbors().map {
                                    searchGrid[it]?.plus(grid[position]!!)
                                        ?: Int.MAX_VALUE
                                }.minOf { it } == searchValue)
                                Pair(position, searchValue)
                            else
                                null
                        },
            targetPosition,
            (searchValue + 1)
        )

fun neighborDistances(
    currentDistance: Int,
    currentPosition: Pair<Int, Int>,
    valuesGrid: SquareGrid<Int>,
    distancesMap: Map<Pair<Int, Int>, Int>
): List<Pair<Pair<Int, Int>, Int>> =
    currentPosition.neighbors().filter { valuesGrid.containsKey(it) }
        .mapNotNull { neighborPosition ->
            if (distancesMap.getOrDefault(
                    neighborPosition,
                    Int.MAX_VALUE
                ) > currentDistance + valuesGrid[neighborPosition]!!
            )
                neighborPosition to (currentDistance + valuesGrid[neighborPosition]!!)
            else
                null
        }

tailrec fun findShortestPath2(
    grid: SquareGrid<Int>,
    distancesFromStart: MutableMap<Pair<Int, Int>, Int>,
    queue: PriorityQueue<Pair<Int, Pair<Int, Int>>>,
    targetPosition: Pair<Int, Int>,
): Int =
    if (queue.isEmpty())
        throw IllegalStateException("queue should not be empty")
    else if (queue.peek().second == targetPosition)
        queue.peek().first
    else
        findShortestPath2(
            grid,
            distancesFromStart,
            queue.apply {
                this.poll().let { (distance, position) ->
                    neighborDistances(distance, position, grid, distancesFromStart)
                        .forEach { (position, distance) ->
                            this.add(Pair(distance, position))
                            distancesFromStart[position] = distance
                        }
                }
            },
            targetPosition
        )


fun main() {
    fun part1(input: List<String>): Int =
        parseInput(input).let { grid ->
            findShortestPath2(
                grid,
                mutableMapOf(Pair(0, 0) to 0),
                PriorityQueue<Pair<Int, Pair<Int, Int>>> { a, b -> a.first - b.first }
                    .apply { add(Pair(0, Pair(0, 0))) },
                Pair(grid.toList().maxOf { it.first.first }, grid.toList().maxOf { it.first.second })
            )
        }

    fun part2(input: List<String>): Int =
        parseInputPart2(input).let { grid ->
            findShortestPath2(
                grid,
                mutableMapOf(Pair(0, 0) to 0),
                PriorityQueue<Pair<Int, Pair<Int, Int>>> { a, b -> a.first - b.first }
                    .apply { add(Pair(0, Pair(0, 0))) },
                Pair(grid.toList().maxOf { it.first.first }, grid.toList().maxOf { it.first.second })
            )
        }

    fun part1old(input: List<String>): Int =
        parseInput(input).let { grid ->
            findShortestPath(
                grid,
                mapOf(Pair(0, 0) to 0),
                Pair(grid.toList().maxOf { it.first.first }, grid.toList().maxOf { it.first.second })
            )
        }

    fun part2old(input: List<String>): Int =
        parseInputPart2(input).let { grid ->
            findShortestPath(
                grid,
                mapOf(Pair(0, 0) to 0),
                Pair(grid.toList().maxOf { it.first.first }, grid.toList().maxOf { it.first.second })
            )
        }

    val testInput = readInput("day15/Day15_test")

    testAnswer(part1(testInput), 40).also { println("Test part 1 passed") }
    testAnswer(
        parseInputPart2(testInput).let { inputGrid ->
            (0..inputGrid.toList().maxOf { it.first.first }).map { inputGrid[it, 0].toString() }
        }.joinToString(""),
        "11637517422274862853338597396444961841755517295286"
    ).also { println("Test part 2 Input passed") }
    testAnswer(part2(testInput), 315).also { println("Test part 2 passed") }

    val input = readInput("day15/Day15")
    val wrongPart1Answers = listOf<Int>(
        421
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
