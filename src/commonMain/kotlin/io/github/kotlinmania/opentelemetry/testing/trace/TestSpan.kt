// port-lint: source testing/trace.rs
package io.github.kotlinmania.opentelemetry.testing.trace

import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.trace.Span
import io.github.kotlinmania.opentelemetry.trace.SpanContext
import io.github.kotlinmania.opentelemetry.trace.Status
import kotlin.time.Instant

public class TestSpan(
    public val context: SpanContext,
) : Span {
    override fun addEventWithTimestamp(
        name: String,
        timestamp: Instant,
        attributes: List<KeyValue>,
    ) {
    }

    override fun spanContext(): SpanContext = context

    override fun isRecording(): Boolean = false

    override fun setAttribute(attribute: KeyValue) {}

    override fun setStatus(status: Status) {}

    override fun updateName(newName: String) {}

    override fun addLink(spanContext: SpanContext, attributes: List<KeyValue>) {}

    override fun endWithTimestamp(timestamp: Instant) {}
}
