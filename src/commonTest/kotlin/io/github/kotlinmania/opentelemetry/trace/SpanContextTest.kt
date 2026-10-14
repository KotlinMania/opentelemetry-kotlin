// port-lint: tests trace/span_context.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.SpanId
import io.github.kotlinmania.opentelemetry.TraceFlags
import io.github.kotlinmania.opentelemetry.TraceId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpanContextTest {
    private data class TraceStateCase(
        val state: TraceState,
        val header: String,
        val key: String,
    )

    private fun traceStateTestData(): List<TraceStateCase> =
        listOf(
            TraceStateCase(
                TraceState.fromKeyValue(listOf("foo" to "bar")),
                "foo=bar",
                "foo",
            ),
            TraceStateCase(
                TraceState.fromKeyValue(listOf("foo" to "", "apple" to "banana")),
                "foo=,apple=banana",
                "apple",
            ),
            TraceStateCase(
                TraceState.fromKeyValue(listOf("foo" to "bar", "apple" to "banana")),
                "foo=bar,apple=banana",
                "apple",
            ),
        )

    @Test
    fun testTraceState() {
        for (testCase in traceStateTestData()) {
            assertEquals(testCase.header, testCase.state.header())

            val newKey = "${testCase.state.get(testCase.key)}-test"
            val updatedTraceState = testCase.state.insert(testCase.key, newKey)
            val updated = "${testCase.key}=$newKey"

            val header = updatedTraceState.header()
            assertTrue(header.startsWith(updated))

            val deletedTraceState = updatedTraceState.delete(testCase.key)
            assertNull(deletedTraceState.get(testCase.key))
        }
    }

    @Test
    fun testTraceStateKey() {
        val testData =
            listOf(
                "123" to true,
                "bar" to true,
                "foo@bar" to true,
                "foo@0123456789abcdef" to false,
                "foo@012345678" to true,
                "FOO@BAR" to false,
                "你好" to false,
            )

        for ((key, expected) in testData) {
            assertEquals(expected, TraceState.validKey(key), "test key: $key")
        }
    }

    @Test
    fun testTraceStateInsert() {
        val traceState = TraceState.fromKeyValue(listOf("foo" to "bar"))
        val inserted = traceState.insert("testkey", "testvalue")
        assertNull(traceState.get("testkey"))
        assertEquals("testvalue", inserted.get("testkey"))
    }

    @Test
    fun testSpanContextNone() {
        val empty = SpanContext.emptyContext()
        assertFalse(empty.isValid())
        assertFalse(empty.isRemote)
        assertFalse(empty.isSampled())
        assertEquals(TraceId.INVALID, empty.traceId)
        assertEquals(SpanId.INVALID, empty.spanId)
        assertEquals(TraceFlags.NOT_SAMPLED, empty.traceFlags)
        assertEquals(TraceState.NONE, empty.traceState)
    }

    @Test
    fun testSpanContextNew() {
        val traceId = TraceId.fromLong(1L)
        val spanId = SpanId.from(11L)
        val flags = TraceFlags.SAMPLED
        val state = TraceState.fromKeyValue(listOf("vendor" to "primary"))

        val spanContext =
            SpanContext.new(
                traceId = traceId,
                spanId = spanId,
                traceFlags = flags,
                isRemote = true,
                traceState = state,
            )

        assertTrue(spanContext.isValid())
        assertTrue(spanContext.isRemote)
        assertTrue(spanContext.isSampled())
        assertEquals(traceId, spanContext.traceId)
        assertEquals(spanId, spanContext.spanId)
        assertEquals(flags, spanContext.traceFlags)
        assertEquals(state, spanContext.traceState)
    }
}
