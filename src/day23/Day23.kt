package day23

import finalAnswerIsNotWrong
import readInput
import testAnswer
import java.util.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis

val SIMPLE_ROOM_STRING_PART1 = """#############
#...........#
###.#.#.#.###
  #.#.#.#.#
  #########"""

val SIMPLE_ROOM_STRING_PART2 = """#############
#...........#
###.#.#.#.###
  #.#.#.#.#
  #.#.#.#.#
  #.#.#.#.#
  #########"""

val ROOM_GRID_PART1: Map<Position, LocationType> = """#############
#.._._._._..#
###A#B#C#D###
  #A#B#C#D#
  #########""".split('\n').flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, c ->
        when (c) {
            '.' -> Position(x, y, 1) to LocationType.HALLWAY
            '_' -> Position(x, y, 1) to LocationType.HALLWAY_INTERSECTION
            'A' -> Position(x, y, 1) to LocationType.A_ROOM
            'B' -> Position(x, y, 1) to LocationType.B_ROOM
            'C' -> Position(x, y, 1) to LocationType.C_ROOM
            'D' -> Position(x, y, 1) to LocationType.D_ROOM
            else -> null
        }
    }
}.toMap()

val ROOM_GRID_PART2: Map<Position, LocationType> = """#############
#.._._._._..#
###A#B#C#D###
  #A#B#C#D#
  #A#B#C#D#
  #A#B#C#D#
  #########""".split('\n').flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, c ->
        when (c) {
            '.' -> Position(x, y, 2) to LocationType.HALLWAY
            '_' -> Position(x, y, 2) to LocationType.HALLWAY_INTERSECTION
            'A' -> Position(x, y, 2) to LocationType.A_ROOM
            'B' -> Position(x, y, 2) to LocationType.B_ROOM
            'C' -> Position(x, y, 2) to LocationType.C_ROOM
            'D' -> Position(x, y, 2) to LocationType.D_ROOM
            else -> null
        }
    }
}.toMap()

enum class LocationType {
    HALLWAY,
    HALLWAY_INTERSECTION,
    A_ROOM,
    B_ROOM,
    C_ROOM,
    D_ROOM
}

enum class AmphipodColor(val cost: Int, val destination: LocationType) {
    A(1, LocationType.A_ROOM),
    B(10, LocationType.B_ROOM),
    C(100, LocationType.C_ROOM),
    D(1000, LocationType.D_ROOM)
}

data class Position(val x: Int, val y: Int, private val part: Int) {
    private val grid = if (part == 1) ROOM_GRID_PART1 else ROOM_GRID_PART2
    val neighbors: List<Position>
        get() = listOf(
            Position(-1, 0, part),
            Position(1, 0, part),
            Position(0, -1, part),
            Position(0, 1, part)
        )
            .map { (dx, dy) -> Position(x + dx, y + dy, part) }
            .filter { grid.containsKey(it) }

    fun manhattanDistanceTo(other: Position): Int =
        abs(x - other.x) + abs(y - other.y)

    fun gridDistanceTo(other: Position): Int =
        (y - 1) + (other.y - 1) + abs((x - other.x))

    val locationType: LocationType?
        get() = grid[this]
}

data class GameState(val bugs: Map<Position, AmphipodColor>, val totalCost: Int, private val part: Int) {
    private val roomString: String = if (part == 1) SIMPLE_ROOM_STRING_PART1 else SIMPLE_ROOM_STRING_PART2
    val isSolved: Boolean
        get() = bugs.all { (position, color) -> bugIsAtDestination(position, color) }

    private fun roomOccupants(roomType: LocationType): List<AmphipodColor> =
        bugs.filter { (position, _) -> position.locationType == roomType }
            .map { it.value }

    private fun bugIsAtDestination(position: Position, bugType: AmphipodColor): Boolean =
        position.locationType == bugType.destination

    private fun isBugLowestRoomPosition(position: Position, bugType: AmphipodColor): Boolean =
        position.locationType == bugType.destination
                && position.neighbors.filter { !bugs.containsKey(it) || bugs[it] != bugType }.size <= 1

    private fun bugCanMoveTo(
        targetLocation: Position,
        currentLocation: Position,
        bugType: AmphipodColor
    ): Boolean =
        !isBugLowestRoomPosition(currentLocation, bugType) && (
                isBugLowestRoomPosition(targetLocation, bugType) || (
                        targetLocation.locationType == LocationType.HALLWAY
                                && currentLocation.locationType != LocationType.HALLWAY
                        )
                )
//                || (
//                targetLocation.locationType == bugType.destination
//                        && roomHasNoIntruders(bugType.destination)
//                        && !isBugLowestRoomPosition(currentLocation, bugType)
//                )

    private fun nextStates(bugPosition: Position, bugType: AmphipodColor): List<GameState> =
        reachablePositions(bugPosition)
            .filter { bugCanMoveTo(it, bugPosition, bugType) }
            .let { nextPositions ->
                if (nextPositions.any { isBugLowestRoomPosition(it, bugType) })
                    nextPositions.filter { isBugLowestRoomPosition(it, bugType) }
                else
                    nextPositions
            }
            .map { nextPosition ->
                GameState(
                    bugs.toMutableMap().apply {
                        remove(bugPosition)
                        put(nextPosition, bugType)
                    },
                    totalCost + (bugPosition.gridDistanceTo(nextPosition) * bugType.cost),
                    part
                )
            }

    private fun reachablePositionsHelper(currentPosition: Position, visited: Set<Position> = setOf()): List<Position> =
        if (bugs.containsKey(currentPosition))
            listOf()
        else
            listOf(currentPosition) + currentPosition.neighbors.filter { !visited.contains(it) }.flatMap {
                reachablePositionsHelper(it, visited + currentPosition)
            }

    private fun reachablePositions(bugPosition: Position): List<Position> =
        bugPosition.neighbors.flatMap { reachablePositionsHelper(it) }

    fun nextStates(): List<GameState> =
        bugs.flatMap { nextStates(it.key, it.value) }

    override fun toString(): String = "costs $totalCost:\n" + roomString.split('\n').mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            if (bugs.containsKey(Position(x, y, part)))
                bugs[Position(x, y, part)]!!.name
            else
                c
        }.joinToString("")
    }.joinToString("\n")
}

tailrec fun findLowestEnergySolution(
    queue: PriorityQueue<GameState>,
    seen: MutableSet<GameState> = mutableSetOf()
): Int =
    if (queue.isEmpty())
        throw IllegalStateException("Failed to solve")
    else if (queue.peek().isSolved)
        queue.peek().totalCost
    else
        findLowestEnergySolution(
            queue.apply {
                poll().nextStates().filter { !seen.contains(it) }.let { nextStates ->
                    seen.addAll(nextStates)
                    queue.addAll(nextStates)
                }
            },
            seen
        )

fun findLowestEnergySolution(initialState: GameState): Int =
    findLowestEnergySolution(
        PriorityQueue<GameState> { a, b -> a.totalCost - b.totalCost }
            .apply { add(initialState) }
    )

fun parseInput(input: List<String>, part: Int): GameState =
    GameState(input.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            when (c) {
                'A' -> Position(x, y, part) to AmphipodColor.A
                'B' -> Position(x, y, part) to AmphipodColor.B
                'C' -> Position(x, y, part) to AmphipodColor.C
                'D' -> Position(x, y, part) to AmphipodColor.D
                else -> null
            }
        }
    }.toMap(), 0, part)

fun main() {
    fun part1(input: List<String>, part: Int = 1): Int =
        findLowestEnergySolution(parseInput(input, part).also { println(it) })

    fun part2(input: List<String>): Int =
        part1(
            input.subList(0, 3) + """  #D#C#B#A#
  #D#B#A#C#""".split('\n') + input.subList(3, input.size),
            2
        )

    val testInput = readInput("day23/test")
//    parseInput(testInput).let { testState ->
//        println(testState)
//        println(testState.nextStates().flatMap { it.nextStates() }.joinToString("\n\n"))
//    }
    testAnswer(part1(testInput), 12521).also { println("Test part 1 passed") }
    testAnswer(part2(testInput), 44169).also { println("Test part 2 passed") }

    val input = readInput("day23/input")
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
