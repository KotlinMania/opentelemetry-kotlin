// port-lint: source global/trace.rs
package io.github.kotlinmania.opentelemetry.global

import io.github.kotlinmania.opentelemetry.Context
import io.github.kotlinmania.opentelemetry.InstrumentationScope
import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.time.Time
import io.github.kotlinmania.opentelemetry.trace.NoopTracerProvider
import io.github.kotlinmania.opentelemetry.trace.Span
import io.github.kotlinmania.opentelemetry.trace.SpanBuilder
import io.github.kotlinmania.opentelemetry.trace.SpanContext
import io.github.kotlinmania.opentelemetry.trace.Status
import io.github.kotlinmania.opentelemetry.trace.Tracer
import io.github.kotlinmania.opentelemetry.trace.TracerProvider
import kotlin.time.Instant

/**
 * Allows a specific [Span] to be used generically by [BoxedSpan] instances.
 */
public interface ObjectSafeSpan {
    public fun addEventWithTimestamp(
        name: String,
        timestamp: Instant,
        attributes: List<KeyValue>,
    )

    public fun spanContext(): SpanContext

    public fun isRecording(): Boolean

    public fun setAttribute(attribute: KeyValue)

    public fun setStatus(status: Status)

    public fun updateName(newName: String)

    public fun addLink(spanContext: SpanContext, attributes: List<KeyValue>)

    public fun endWithTimestamp(timestamp: Instant)

    public fun end() {
        endWithTimestamp(Time.now())
    }
}

/**
 * Allows a specific [Tracer] to be used generically by [BoxedTracer] instances.
 */
public interface ObjectSafeTracer {
    public fun buildWithContextBoxed(
        builder: SpanBuilder,
        parentCx: Context,
    ): ObjectSafeSpan
}

/**
 * Allows a specific [TracerProvider] to be used generically by [GlobalTracerProvider].
 */
public interface ObjectSafeTracerProvider {
    public fun boxedTracer(scope: InstrumentationScope): ObjectSafeTracer
}

/**
 * Wraps a [Span] so it can be used generically without knowing the underlying type.
 */
public class BoxedSpan(
    private val inner: Span,
) : Span,
    ObjectSafeSpan {
    override fun addEvent(name: String, attributes: List<KeyValue>) {
        inner.addEvent(name, attributes)
    }

    override fun recordError(err: Throwable) {
        inner.recordError(err)
    }

    override fun addEventWithTimestamp(
        name: String,
        timestamp: Instant,
        attributes: List<KeyValue>,
    ) {
        inner.addEventWithTimestamp(name, timestamp, attributes)
    }

    override fun spanContext(): SpanContext = inner.spanContext()

    override fun isRecording(): Boolean = inner.isRecording()

    override fun setAttribute(attribute: KeyValue) {
        inner.setAttribute(attribute)
    }

    override fun setAttributes(attributes: Iterable<KeyValue>) {
        inner.setAttributes(attributes)
    }

    override fun setStatus(status: Status) {
        inner.setStatus(status)
    }

    override fun updateName(newName: String) {
        inner.updateName(newName)
    }

    override fun addLink(spanContext: SpanContext, attributes: List<KeyValue>) {
        inner.addLink(spanContext, attributes)
    }

    override fun end() {
        inner.end()
    }

    override fun endWithTimestamp(timestamp: Instant) {
        inner.endWithTimestamp(timestamp)
    }

    public companion object {
        public fun new(span: Span): BoxedSpan = BoxedSpan(span)
    }
}

/**
 * Wraps a [Tracer] so it can be used generically without knowing the underlying type.
 */
public class BoxedTracer(
    private val inner: Tracer,
) : Tracer,
    ObjectSafeTracer {
    override fun start(name: String): Span = inner.start(name)

    override fun startWithContext(name: String, parentCx: Context): Span =
        inner.startWithContext(name, parentCx)

    override fun spanBuilder(name: String): SpanBuilder = inner.spanBuilder(name)

    override fun build(builder: SpanBuilder): Span = inner.build(builder)

    override fun buildWithContext(builder: SpanBuilder, parentCx: Context): Span =
        BoxedSpan(inner.buildWithContext(builder, parentCx))

    override fun buildWithContextBoxed(
        builder: SpanBuilder,
        parentCx: Context,
    ): ObjectSafeSpan =
        BoxedSpan(inner.buildWithContext(builder, parentCx))

    override fun <T> inSpan(name: String, f: (Context) -> T): T =
        inner.inSpan(name, f)

    public companion object {
        public fun new(tracer: Tracer): BoxedTracer = BoxedTracer(tracer)
    }
}

/**
 * Represents the globally configured [TracerProvider] instance for this application.
 */
public class GlobalTracerProvider(
    private var provider: TracerProvider,
) : TracerProvider,
    ObjectSafeTracerProvider {
    override fun tracer(name: String): Tracer =
        BoxedTracer(provider.tracer(name))

    override fun tracerWithScope(scope: InstrumentationScope): Tracer =
        BoxedTracer(provider.tracerWithScope(scope))

    override fun boxedTracer(scope: InstrumentationScope): ObjectSafeTracer =
        BoxedTracer(provider.tracerWithScope(scope))

    public fun setProvider(newProvider: TracerProvider) {
        provider = newProvider
    }

    public companion object {
        public fun new(provider: TracerProvider): GlobalTracerProvider =
            GlobalTracerProvider(provider)
    }
}

private val globalProvider: GlobalTracerProvider =
    GlobalTracerProvider.new(NoopTracerProvider.new())

/**
 * Returns an instance of the currently configured global [TracerProvider].
 */
public fun tracerProvider(): GlobalTracerProvider = globalProvider

/**
 * Creates a named instance of [Tracer] via the configured [GlobalTracerProvider].
 */
public fun tracer(name: String): Tracer =
    tracerProvider().tracer(name)

/**
 * Creates a [Tracer] with the given instrumentation scope via the configured [GlobalTracerProvider].
 */
public fun tracerWithScope(scope: InstrumentationScope): Tracer =
    tracerProvider().tracerWithScope(scope)

/**
 * Sets the given [TracerProvider] instance as the current global provider.
 */
public fun setTracerProvider(newProvider: TracerProvider) {
    globalProvider.setProvider(newProvider)
}
