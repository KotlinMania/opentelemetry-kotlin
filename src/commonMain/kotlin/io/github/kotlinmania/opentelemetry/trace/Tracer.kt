// port-lint: source trace/tracer.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.Context
import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.SpanId
import io.github.kotlinmania.opentelemetry.TraceId
import kotlin.time.Instant

/**
 * The interface for constructing [Span]s.
 */
public interface Tracer {
    /**
     * Starts a new [Span].
     */
    public fun start(name: String): Span =
        Context.mapCurrent { cx -> startWithContext(name, cx) }

    /**
     * Starts a new [Span] with a given context.
     */
    public fun startWithContext(name: String, parentCx: Context): Span =
        buildWithContext(SpanBuilder.fromName(name), parentCx)

    /**
     * Creates a span builder.
     */
    public fun spanBuilder(name: String): SpanBuilder = SpanBuilder.fromName(name)

    /**
     * Start a [Span] from a [SpanBuilder].
     */
    public fun build(builder: SpanBuilder): Span =
        Context.mapCurrent { cx -> buildWithContext(builder, cx) }

    /**
     * Start a span from a [SpanBuilder] with a parent context.
     */
    public fun buildWithContext(builder: SpanBuilder, parentCx: Context): Span

    /**
     * Start a new span and execute the given block with reference to the context
     * in which the span is active.
     */
    public fun <T> inSpan(name: String, f: (Context) -> T): T {
        val span = start(name)
        val cx = Context.currentWithSpan(span)
        val guard = cx.attach()
        return try {
            f(cx)
        } finally {
            guard.close()
        }
    }
}

/**
 * [SpanBuilder] allows span attributes to be configured before the span has started.
 */
public class SpanBuilder(
    public var traceId: TraceId? = null,
    public var spanId: SpanId? = null,
    public var spanKind: SpanKind? = null,
    public var name: String = "",
    public var startTime: Instant? = null,
    public var endTime: Instant? = null,
    public var attributes: List<KeyValue>? = null,
    public var events: List<Event>? = null,
    public var links: List<Link>? = null,
    public var status: Status = Status.Unset,
    public var samplingResult: SamplingResult? = null,
) {
    public fun withTraceId(traceId: TraceId): SpanBuilder {
        this.traceId = traceId
        return this
    }

    public fun withSpanId(spanId: SpanId): SpanBuilder {
        this.spanId = spanId
        return this
    }

    public fun withKind(spanKind: SpanKind): SpanBuilder {
        this.spanKind = spanKind
        return this
    }

    public fun withStartTime(startTime: Instant): SpanBuilder {
        this.startTime = startTime
        return this
    }

    public fun withEndTime(endTime: Instant): SpanBuilder {
        this.endTime = endTime
        return this
    }

    public fun withAttributes(attributes: Iterable<KeyValue>): SpanBuilder {
        val list = this.attributes?.toMutableList() ?: mutableListOf()
        for (attr in attributes) {
            list.add(attr)
        }
        this.attributes = list
        return this
    }

    public fun withEvents(events: List<Event>): SpanBuilder {
        val list = this.events?.toMutableList() ?: mutableListOf()
        list.addAll(events)
        this.events = list
        return this
    }

    public fun withLinks(links: List<Link>): SpanBuilder {
        val filtered = links.filter { it.spanContext.isValid() }
        val list = this.links?.toMutableList() ?: mutableListOf()
        list.addAll(filtered)
        this.links = list
        return this
    }

    public fun withStatus(status: Status): SpanBuilder {
        this.status = status
        return this
    }

    public fun withSamplingResult(samplingResult: SamplingResult): SpanBuilder {
        this.samplingResult = samplingResult
        return this
    }

    public fun start(tracer: Tracer): Span =
        Context.mapCurrent { cx -> tracer.buildWithContext(this, cx) }

    public fun startWithContext(tracer: Tracer, parentCx: Context): Span =
        tracer.buildWithContext(this, parentCx)

    public companion object {
        public fun fromName(name: String): SpanBuilder = SpanBuilder(name = name)
    }
}

/**
 * The result of sampling logic for a given span.
 */
public data class SamplingResult(
    /**
     * The decision about whether or not to sample.
     */
    public val decision: SamplingDecision,
    /**
     * Extra attributes to be added to the span by the sampler.
     */
    public val attributes: List<KeyValue> = emptyList(),
    /**
     * Trace state from parent context, may be modified by samplers.
     */
    public val traceState: TraceState = TraceState.NONE,
)

/**
 * Decision about whether or not to sample.
 */
public enum class SamplingDecision {
    /**
     * Span will not be recorded and all events and attributes will be dropped.
     */
    Drop,

    /**
     * Span data will be recorded, but not exported.
     */
    RecordOnly,

    /**
     * Span data will be recorded and exported.
     */
    RecordAndSample,
}
