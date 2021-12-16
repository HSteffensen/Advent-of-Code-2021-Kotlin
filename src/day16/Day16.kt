package day16

import finalAnswerIsNotWrong
import readInput
import testAnswer
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

val versionBitsRange = 0..2
val typeIdBitsRange = 3..5
const val literalValueStartIndex = 6
val lengthIdBitsRange = 6..6
const val lengthBitsStartIndex = 7
const val lengthBits0Size = 15
const val lengthBits1Size = 11
val bitsHexMap = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)

interface BitsPacket {
    val version: Int
    val typeId: Int
    val totalLength: Int
    val value: Long
}

data class BitsLiteralPacket(
    val bits: String
) : BitsPacket {
    private val versionBits = bits.substring(versionBitsRange)
    override val version = versionBits.toInt(2)
    override val typeId = packetTypeId(bits)
    private val valueBits = bits.substring(literalValueStartIndex)
    override val value = valueBits.chunked(5).joinToString("") { if (it.length == 5) it.substring(1) else "" }.toLong(2)
    override val totalLength = bits.length
}

data class BitsOperatorPacket(
    val bits: String,
    val subpackets: List<BitsPacket> = listOf()
) : BitsPacket {
    private val versionBits = bits.substring(versionBitsRange)
    override val version = versionBits.toInt(2)
    override val typeId = packetTypeId(bits)
    val type: OperatorType = when (typeId) {
        0 -> OperatorType.SUM
        1 -> OperatorType.PRODUCT
        2 -> OperatorType.MINIMUM
        3 -> OperatorType.MAXIMUM
        5 -> OperatorType.GREATER_THAN
        6 -> OperatorType.LESS_THAN
        7 -> OperatorType.EQUAL_TO
        else -> throw IllegalStateException("there should only be these operator typeIds possible")
    }
    private val lengthTypeBits = bits.substring(lengthIdBitsRange)
    val lengthType = if (lengthTypeBits.toInt(2) == 0) LengthType.TOTAL_BITS else LengthType.TOTAL_DIRECT_SUBPACKETS
    private val lengthBits =
        bits.substring(lengthBitsStartIndex until (if (lengthType == LengthType.TOTAL_BITS) lengthBitsStartIndex + lengthBits0Size else lengthBitsStartIndex + lengthBits1Size))
    val length = lengthBits.toInt(2)
    override val totalLength = bits.length + subpackets.sumOf { it.totalLength }
    override val value: Long
        get() = evaluateOperatorPacket(this)

    enum class LengthType {
        TOTAL_BITS,
        TOTAL_DIRECT_SUBPACKETS,
    }

    enum class OperatorType {
        SUM,
        PRODUCT,
        MINIMUM,
        MAXIMUM,
        GREATER_THAN,
        LESS_THAN,
        EQUAL_TO,
    }
}

fun evaluateOperatorPacket(packet: BitsOperatorPacket): Long =
    packet.subpackets.map { it.value }.reduce { a, b ->
        when (packet.type) {
            BitsOperatorPacket.OperatorType.SUM -> a + b
            BitsOperatorPacket.OperatorType.PRODUCT -> a * b
            BitsOperatorPacket.OperatorType.MINIMUM -> min(a, b)
            BitsOperatorPacket.OperatorType.MAXIMUM -> max(a, b)
            BitsOperatorPacket.OperatorType.GREATER_THAN -> if (a > b) 1 else 0
            BitsOperatorPacket.OperatorType.LESS_THAN -> if (a < b) 1 else 0
            BitsOperatorPacket.OperatorType.EQUAL_TO -> if (a == b) 1 else 0
        }
    }

fun packetTypeId(bits: String): Int =
    bits.substring(typeIdBitsRange).toInt(2)

fun parseInputHexToBits(hexInput: String): String =
    hexInput.map { bitsHexMap[it]!! }.joinToString("")

fun isTrailingZeroes(bits: String): Boolean =
    bits.fold(true) { a, b -> a && b == '0' }

fun indexAfterFirstPacket(bits: String): Int =
    if (packetTypeId(bits) == 4)
        literalValueStartIndex + (
                5 * (
                        bits.substring(literalValueStartIndex)
                            .chunked(5) { it.first() }
                            .takeWhile { it == '1' }.size
                                + 1
                        )
                )
    else
        lengthBitsStartIndex + (if (bits.substring(lengthIdBitsRange) == "0") lengthBits0Size else lengthBits1Size)

fun firstPacketBits(bits: String): String =
    bits.substring(0 until indexAfterFirstPacket(bits))

fun depthIndent(depth: Int): String = (0..depth).joinToString("") { "  " }

fun parsePacketBits(
    bits: String,
    previousPackets: List<BitsPacket> = listOf(),
    limit: Int? = null,
    depth: Int = 0,
    doPrint: Boolean = false
): Pair<List<BitsPacket>, String> =
    if (isTrailingZeroes(bits) || limit == 0)
        Pair(
            previousPackets,
            bits
        ).also {
            if (doPrint) println(
                "${depthIndent(depth)}returning with ${bits.length} remaining bits which ${
                    if (isTrailingZeroes(
                            bits
                        )
                    ) "ARE" else "are NOT"
                } all zeroes"
            )
        }
    else if (packetTypeId(bits) == 4)
        parsePacketBits(
            bits.substring(indexAfterFirstPacket(bits)),
            previousPackets + BitsLiteralPacket(firstPacketBits(bits)).also { if (doPrint) println("${depthIndent(depth)}$it") },
            limit?.minus(1),
            depth,
            doPrint
        )
    else
        BitsOperatorPacket(firstPacketBits(bits))
            .let { operatorPacketWithoutSubpackets ->
                if (operatorPacketWithoutSubpackets.also { if (doPrint) println("${depthIndent(depth)}$it ${it.lengthType} ${it.length}") }.lengthType == BitsOperatorPacket.LengthType.TOTAL_BITS)
                    BitsOperatorPacket(
                        firstPacketBits(bits),
                        parsePacketBits(
                            bits.substring(indexAfterFirstPacket(bits) until indexAfterFirstPacket(bits) + operatorPacketWithoutSubpackets.length),
                            depth = depth + 1,
                            doPrint = doPrint
                        ).first
                    ) to bits.substring(indexAfterFirstPacket(bits) + operatorPacketWithoutSubpackets.length)
                else
                    parsePacketBits(
                        bits.substring(indexAfterFirstPacket(bits)),
                        listOf(),
                        operatorPacketWithoutSubpackets.length,
                        depth + 1,
                        doPrint
                    ).let { (packets, remainingBits) ->
                        BitsOperatorPacket(firstPacketBits(bits), packets) to remainingBits
                    }
            }
            .let { (operatorPacketWithSubpackets, remainingBits) ->
                parsePacketBits(
                    remainingBits,
                    previousPackets + operatorPacketWithSubpackets,
                    limit?.minus(1),
                    depth,
                    doPrint
                )
            }

fun sumOfVersions(packets: List<BitsPacket>): Int =
    packets.sumOf {
        when (it) {
            is BitsLiteralPacket -> it.version
            is BitsOperatorPacket -> it.version + sumOfVersions(it.subpackets)
            else -> throw IllegalStateException("there should be only two possible BitsPacket types")
        }
    }

fun sumOfLiterals(packets: List<BitsPacket>): Long =
    packets.sumOf {
        when (it) {
            is BitsLiteralPacket -> it.value
            is BitsOperatorPacket -> sumOfLiterals(it.subpackets)
            else -> throw IllegalStateException("there should be only two possible BitsPacket types")
        }
    }

fun valueTreeString(packet: BitsPacket, prefix: String = ""): List<String> =
    when (packet) {
        is BitsLiteralPacket -> valueTreeString(packet, "$prefix  ")
        is BitsOperatorPacket -> valueTreeString(packet, "$prefix| ")
        else -> throw IllegalStateException("there should be only two possible BitsPacket types")
    }

fun valueTreeString(packet: BitsLiteralPacket, prefix: String = ""): List<String> =
    listOf(prefix + "===" + packet.value.toString())

fun valueTreeString(packet: BitsOperatorPacket, prefix: String = ""): List<String> =
    listOf(
        prefix
                + when (packet.type) {
            BitsOperatorPacket.OperatorType.SUM -> "+ "
            BitsOperatorPacket.OperatorType.PRODUCT -> "* "
            BitsOperatorPacket.OperatorType.MINIMUM -> "min "
            BitsOperatorPacket.OperatorType.MAXIMUM -> "MAX "
            BitsOperatorPacket.OperatorType.GREATER_THAN -> "> "
            BitsOperatorPacket.OperatorType.LESS_THAN -> "< "
            BitsOperatorPacket.OperatorType.EQUAL_TO -> "= "
        }
                + packet.value.toString()
                + " (v=${packet.version}, t=${packet.type}, lt=${packet.lengthType}, l=${packet.length}) "
                + packet.subpackets.map { it.value }.toString()
    ) + packet.subpackets.flatMap { valueTreeString(it, prefix) }

fun main() {
    fun parseInputToPackets(input: List<String>) =
        parsePacketBits(parseInputHexToBits(input.single())).first

    fun part1(input: List<String>): Int =
        sumOfVersions(parseInputToPackets(input))

    fun part2(input: List<String>): Long =
        parseInputToPackets(input).single().value

    fun part1(input: String): Int = part1(listOf(input))

    fun part2(input: String): Long = part2(listOf(input))

    initialTests()

    testAnswer(part1("8A004A801A8002F478"), 16).also { println("Test part 1-1 passed") }
    testAnswer(part1("620080001611562C8802118E34"), 12).also { println("Test part 1-2 passed") }
    testAnswer(part1("C0015000016115A2E0802F182340"), 23).also { println("Test part 1-3 passed") }
    testAnswer(part1("A0016C880162017C3686B18A3D4780"), 31).also { println("Test part 1-4 passed") }

    testAnswer(part2("C200B40A82"), 3).also { println("Test part 2-1 passed") }
    testAnswer(part2("04005AC33890"), 54).also { println("Test part 2-2 passed") }
    testAnswer(part2("880086C3E88112"), 7).also { println("Test part 2-3 passed") }
    testAnswer(part2("CE00C43D881120"), 9).also { println("Test part 2-4 passed") }
    testAnswer(part2("D8005AC2A8F0"), 1).also { println("Test part 2-5 passed") }
    testAnswer(part2("F600BC2D8F"), 0).also { println("Test part 2-6 passed") }
    testAnswer(part2("9C005AC2F8F0"), 0).also { println("Test part 2-7 passed") }
    testAnswer(part2("9C0141080250320F1802104A08"), 1).also { println("Test part 2-8 passed") }

    val input = readInput("day16/Day16")
    val wrongPart1Answers = listOf<Int>(
    )
    measureTimeMillis {
        println("Part 1: ${finalAnswerIsNotWrong(part1(input), wrongPart1Answers)}")
    }.also { println("\ttook $it milliseconds") }

    val wrongPart2Answers = listOf(
        54L
    )
    measureTimeMillis {
        println("Part 2: ${finalAnswerIsNotWrong(part2(input), wrongPart2Answers)}")
    }.also { println("\ttook $it milliseconds") }
}

fun initialTests() {
    testAnswer(parseInputHexToBits("D2FE28"), "110100101111111000101000").also { println("parseInputHexToBits passed") }
    testAnswer(
        "110100101111111000101000".let { it.substring(indexAfterFirstPacket(it)) },
        "000"
    ).also { println("indexAfterFirstPacket passed") }
    testAnswer(
        "00111000000000000110111101000101001010010001001000000000".let { it.substring(indexAfterFirstPacket(it)) },
        "1101000101001010010001001000000000"
    ).also { println("indexAfterFirstPacket passed") }
    testAnswer(
        "11101110000000001101010000001100100000100011000001100000".let { it.substring(indexAfterFirstPacket(it)) },
        "01010000001100100000100011000001100000"
    ).also { println("indexAfterFirstPacket passed") }
    BitsLiteralPacket("110100101111111000101")
        .also {
            check(it.version == 6) { "BitsLiteralPacket wrong version: $it" }
            check(it.typeId == 4) { "BitsLiteralPacket wrong typeId: $it" }
            check(it.value == 2021L) { "BitsLiteralPacket wrong value: $it" }
        }.also { println("BitsLiteralPacket passed") }
    BitsOperatorPacket("0011100000000000011011")
        .also {
            check(it.version == 1) { "BitsOperatorPacket wrong version: $it" }
            check(it.typeId == 6) { "BitsOperatorPacket wrong typeId: $it" }
            check(it.lengthType == BitsOperatorPacket.LengthType.TOTAL_BITS) { "BitsOperatorPacket wrong lengthId: $it" }
            check(it.length == 27) { "BitsOperatorPacket wrong length: $it" }
        }.also { println("BitsOperatorPacket passed") }
    BitsOperatorPacket("111011100000000011")
        .also {
            check(it.version == 7) { "BitsOperatorPacket wrong version: $it" }
            check(it.typeId == 3) { "BitsOperatorPacket wrong typeId: $it" }
            check(it.lengthType == BitsOperatorPacket.LengthType.TOTAL_DIRECT_SUBPACKETS) { "BitsOperatorPacket wrong lengthId: $it" }
            check(it.length == 3) { "BitsOperatorPacket wrong length: $it" }
        }.also { println("BitsOperatorPacket passed") }

}
