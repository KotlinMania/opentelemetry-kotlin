// port-lint: tests trace/noop.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.Context
import io.github.kotlinmania.opentelemetry.SpanId
import io.github.kotlinmania.opentelemetry.TraceFlags
import io.github.kotlinmania.opentelemetry.TraceId
import io.github.kotlinmania.opentelemetry.testing.trace.TestSpan
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoopTest {
    private fun validSpanContext(): SpanContext =
        SpanContext.new(
            traceId = TraceId.fromLong(42L),
            spanId = SpanId.from(42L),
            traceFlags = TraceFlags.default(),
            isRemote = true,
            traceState = TraceState.default(),
        )

    @Test
    fun noopTracerDefaultsToInvalidSpan() {
        val tracer = NoopTracer.new()
        val span = tracer.startWithContext("foo", Context.new())
        assertFalse(span.spanContext().isValid())
    }

    @Test
    fun noopTracerPropagatesValidSpanContextFromBuilder() {
        val tracer = NoopTracer.new()
        val builder = tracer.spanBuilder("foo")
        val context = Context.new().withSpan(TestSpan(validSpanContext()))
        val span = tracer.buildWithContext(builder, context)
        assertTrue(span.spanContext().isValid())
    }

    @Test
    fun noopTracerPropagatesValidSpanContextFromExplicitlySpecifiedContext() {
        val tracer = NoopTracer.new()
        val cx = Context.new().withSpan(NoopSpan(validSpanContext()))
        val span = tracer.startWithContext("foo", cx)
        assertTrue(span.spanContext().isValid())
    }

    @Test
    fun noopTracerPropagatesValidSpanContextFromRemoteSpanContext() {
        val tracer = NoopTracer.new()
        val cx = Context.new().withRemoteSpanContext(validSpanContext())
        val span = tracer.startWithContext("foo", cx)
        assertTrue(span.spanContext().isValid())
    }
}
