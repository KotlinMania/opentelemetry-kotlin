// port-lint: source trace/noop.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.Context
import io.github.kotlinmania.opentelemetry.InstrumentationScope
import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.propagation.Extractor
import io.github.kotlinmania.opentelemetry.propagation.FieldIter
import io.github.kotlinmania.opentelemetry.propagation.Injector
import io.github.kotlinmania.opentelemetry.propagation.TextMapPropagator
import kotlin.time.Instant

/**
 * A no-op instance of a [TracerProvider].
 */
public class NoopTracerProvider : TracerProvider {
    override fun tracerWithScope(scope: InstrumentationScope): Tracer = NoopTracer.new()

    public companion object {
        public fun new(): NoopTracerProvider = NoopTracerProvider()
    }
}

/**
 * A no-op instance of a [Span].
 */
public class NoopSpan(
    private val spanContext: SpanContext = SpanContext.NONE,
) : Span {
    override fun addEvent(name: String, attributes: List<KeyValue>) {
        // Ignored
    }

    override fun addEventWithTimestamp(
        name: String,
        timestamp: Instant,
        attributes: List<KeyValue>,
    ) {
        // Ignored
    }

    override fun spanContext(): SpanContext = spanContext

    override fun isRecording(): Boolean = false

    override fun setAttribute(attribute: KeyValue) {
        // Ignored
    }

    override fun setStatus(status: Status) {
        // Ignored
    }

    override fun updateName(newName: String) {
        // Ignored
    }

    override fun addLink(spanContext: SpanContext, attributes: List<KeyValue>) {
        // Ignored
    }

    override fun endWithTimestamp(timestamp: Instant) {
        // Ignored
    }

    public companion object {
        public val DEFAULT: NoopSpan = NoopSpan(SpanContext.NONE)
    }
}

/**
 * A no-op instance of a [Tracer].
 */
public class NoopTracer : Tracer {
    override fun buildWithContext(builder: SpanBuilder, parentCx: Context): Span =
        NoopSpan(parentCx.span().spanContext())

    public companion object {
        public fun new(): NoopTracer = NoopTracer()
    }
}

/**
 * A no-op instance of a [TextMapPropagator].
 */
public class NoopTextMapPropagator : TextMapPropagator {
    override fun injectContext(cx: Context, injector: Injector) {
        // Ignored
    }

    override fun extractWithContext(cx: Context, extractor: Extractor): Context =
        Context.current()

    override fun fields(): FieldIter = FieldIter(emptyList())

    public companion object {
        public fun new(): NoopTextMapPropagator = NoopTextMapPropagator()
    }
}
