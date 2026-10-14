// port-lint: tests trace_context.rs
package io.github.kotlinmania.opentelemetry

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TraceContextTest {
    private data class TraceIdTestCase(
        val hex: String,
        val bytes: ByteArray,
    )

    private data class SpanIdTestCase(
        val id: SpanId,
        val hex: String,
        val bytes: ByteArray,
    )

    private fun traceIdTestData(): List<TraceIdTestCase> =
        listOf(
            TraceIdTestCase(
                "00000000000000000000000000000000",
                byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            ),
            TraceIdTestCase(
                "0000000000000000000000000000002a",
                byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 42),
            ),
            TraceIdTestCase(
                "5f467fe7bf42676c05e20ba4a90e448e",
                byteArrayOf(95, 70, 127, 231.toByte(), 191.toByte(), 66, 103, 108, 5, 226.toByte(), 11, 164.toByte(), 169.toByte(), 14, 68, 142.toByte()),
            ),
        )

    private fun spanIdTestData(): List<SpanIdTestCase> =
        listOf(
            SpanIdTestCase(
                SpanId(0L),
                "0000000000000000",
                byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            ),
            SpanIdTestCase(
                SpanId(42L),
                "000000000000002a",
                byteArrayOf(0, 0, 0, 0, 0, 0, 0, 42),
            ),
            SpanIdTestCase(
                SpanId(5508496025762705295L),
                "4c721bf33e3caf8f",
                byteArrayOf(76, 114, 27, 243.toByte(), 62, 60, 175.toByte(), 143.toByte()),
            ),
        )

    @Test
    fun testTraceFlags() {
        val notSampled = TraceFlags.NOT_SAMPLED
        assertFalse(notSampled.isSampled())
        assertEquals(0, notSampled.toU8())

        val sampled = TraceFlags.SAMPLED
        assertTrue(sampled.isSampled())
        assertEquals(1, sampled.toU8())

        val toggled = notSampled.withSampled(true)
        assertTrue(toggled.isSampled())
        assertEquals(sampled, toggled)

        val untoggled = toggled.withSampled(false)
        assertFalse(untoggled.isSampled())
        assertEquals(notSampled, untoggled)
    }

    @Test
    fun testTraceId() {
        for (case in traceIdTestData()) {
            val fromHex = TraceId.fromHex(case.hex)
            val fromBytes = TraceId.fromBytes(case.bytes)

            assertEquals(case.hex, fromHex.toString())
            assertEquals(case.hex, fromBytes.toString())
            assertContentEquals(case.bytes, fromHex.toBytes())
            assertContentEquals(case.bytes, fromBytes.toBytes())
            assertEquals(fromHex, fromBytes)
        }
    }

    @Test
    fun testSpanId() {
        for (case in spanIdTestData()) {
            val fromHex = SpanId.fromHex(case.hex)
            val fromBytes = SpanId.fromBytes(case.bytes)

            assertEquals(case.hex, case.id.toString())
            assertEquals(case.hex, fromHex.toString())
            assertEquals(case.hex, fromBytes.toString())
            assertContentEquals(case.bytes, case.id.toBytes())
            assertContentEquals(case.bytes, fromHex.toBytes())
            assertContentEquals(case.bytes, fromBytes.toBytes())
            assertEquals(case.id, fromHex)
            assertEquals(case.id, fromBytes)
        }
    }
}
