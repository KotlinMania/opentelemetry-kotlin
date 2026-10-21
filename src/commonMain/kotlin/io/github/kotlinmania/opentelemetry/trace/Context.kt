// port-lint: source trace/context.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.Context
import io.github.kotlinmania.opentelemetry.ContextGuard
import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.time.Time
import kotlin.time.Instant

private val NOOP_SPAN: SynchronizedSpan = SynchronizedSpan(SpanContext.NONE, null)

/**
 * A reference to the currently active span in this context.
 */
public class SpanRef internal constructor(
    internal val span: SynchronizedSpan,
) {
    private inline fun withInnerMut(f: (Span) -> Unit) {
        val inner = span.inner
        if (inner != null) {
            f(inner)
        }
    }

    /**
     * Record an event in the context this span.
     */
    public fun addEvent(name: String, attributes: List<KeyValue> = emptyList()) {
        withInnerMut { it.addEvent(name, attributes) }
    }

    /**
     * Record an error as an event for this span.
     */
    public fun recordError(err: Throwable) {
        withInnerMut { it.recordError(err) }
    }

    /**
     * Record an event with a timestamp in the context this span.
     */
    public fun addEventWithTimestamp(
        name: String,
        timestamp: Instant,
        attributes: List<KeyValue> = emptyList(),
    ) {
        withInnerMut { it.addEventWithTimestamp(name, timestamp, attributes) }
    }

    /**
     * A reference to the [SpanContext] for this span.
     */
    public fun spanContext(): SpanContext = span.spanContext

    /**
     * Returns true if this span is recording information.
     */
    public fun isRecording(): Boolean = span.inner?.isRecording() ?: false

    /**
     * Set an attribute of this span.
     */
    public fun setAttribute(attribute: KeyValue) {
        withInnerMut { it.setAttribute(attribute) }
    }

    /**
     * Set multiple attributes of this span.
     */
    public fun setAttributes(attributes: Iterable<KeyValue>) {
        withInnerMut { it.setAttributes(attributes) }
    }

    /**
     * Sets the status of this [Span].
     */
    public fun setStatus(status: Status) {
        withInnerMut { it.setStatus(status) }
    }

    /**
     * Updates the span's name.
     */
    public fun updateName(newName: String) {
        withInnerMut { it.updateName(newName) }
    }

    /**
     * Adds a [Link] to another [SpanContext].
     */
    public fun addLink(spanContext: SpanContext, attributes: List<KeyValue> = emptyList()) {
        withInnerMut { it.addLink(spanContext, attributes) }
    }

    /**
     * Signals that the operation described by this span has now ended.
     */
    public fun end() {
        endWithTimestamp(Time.now())
    }

    /**
     * Signals that the operation described by this span ended at the given time.
     */
    public fun endWithTimestamp(timestamp: Instant) {
        withInnerMut { it.endWithTimestamp(timestamp) }
    }
}

public class SynchronizedSpan internal constructor(
    public val spanContext: SpanContext,
    public val inner: Span?,
) {
    public companion object {
        public fun from(spanContext: SpanContext): SynchronizedSpan =
            SynchronizedSpan(spanContext, null)

        public fun from(span: Span): SynchronizedSpan =
            SynchronizedSpan(span.spanContext(), span)
    }
}

/**
 * Methods for storing and retrieving trace data in a [Context].
 */
public interface TraceContextExt {
    public fun currentWithSpan(span: Span): Context

    public fun withSpan(span: Span): Context

    public fun span(): SpanRef

    public fun hasActiveSpan(): Boolean

    public fun withRemoteSpanContext(spanContext: SpanContext): Context
}

/**
 * Methods for storing and retrieving trace data in a [Context].
 */
public fun Context.withSpan(span: Span): Context {
    val sync = SynchronizedSpan.from(span)
    return withValue(sync)
}

public fun Context.span(): SpanRef {
    val sync = get(SynchronizedSpan::class)
    return if (sync != null) SpanRef(sync) else SpanRef(NOOP_SPAN)
}

public fun Context.hasActiveSpan(): Boolean =
    get(SynchronizedSpan::class) != null

public fun Context.withRemoteSpanContext(spanContext: SpanContext): Context {
    val sync = SynchronizedSpan.from(spanContext)
    return withValue(sync)
}

public fun Context.Companion.currentWithSpan(span: Span): Context =
    current().withSpan(span)

/**
 * Mark a given [Span] as active.
 */
public fun markSpanAsActive(span: Span): ContextGuard {
    val cx = Context.currentWithSpan(span)
    return cx.attach()
}

/**
 * Executes a closure with a reference to this thread's current span.
 */
public fun <T> getActiveSpan(f: (SpanRef) -> T): T =
    Context.mapCurrent { cx -> f(cx.span()) }
