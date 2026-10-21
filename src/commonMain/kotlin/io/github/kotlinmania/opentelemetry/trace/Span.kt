// port-lint: source trace/span.rs
package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.time.Time
import kotlin.time.Instant

/**
 * The interface for a single operation within a trace.
 *
 * Spans can be nested to form a trace tree. Each trace contains a root span,
 * which typically describes the entire operation and, optionally, one or more
 * sub-spans for its sub-operations.
 */
public interface Span {
    /**
     * Record an event in the context this span.
     */
    public fun addEvent(name: String, attributes: List<KeyValue> = emptyList()) {
        addEventWithTimestamp(name, Time.now(), attributes)
    }

    /**
     * Record an error as an event for this span.
     *
     * An additional call to [Span.setStatus] is required if the status of the
     * span should be set to error, as this method does not change the span status.
     *
     * If this span is not being recorded then this method does nothing.
     */
    public fun recordError(err: Throwable) {
        if (isRecording()) {
            val attributes = listOf(KeyValue.new("exception.message", err.message ?: err.toString()))
            addEvent("exception", attributes)
        }
    }

    /**
     * Record an event with a timestamp in the context this span.
     */
    public fun addEventWithTimestamp(
        name: String,
        timestamp: Instant,
        attributes: List<KeyValue> = emptyList(),
    )

    /**
     * A reference to the [SpanContext] for this span.
     */
    public fun spanContext(): SpanContext

    /**
     * Returns true if this span is recording information.
     */
    public fun isRecording(): Boolean

    /**
     * Set an attribute of this span.
     */
    public fun setAttribute(attribute: KeyValue)

    /**
     * Set multiple attributes of this span.
     */
    public fun setAttributes(attributes: Iterable<KeyValue>) {
        if (isRecording()) {
            for (attr in attributes) {
                setAttribute(attr)
            }
        }
    }

    /**
     * Sets the status of this [Span].
     */
    public fun setStatus(status: Status)

    /**
     * Updates the span's name.
     */
    public fun updateName(newName: String)

    /**
     * Adds [Link] to another [SpanContext].
     */
    public fun addLink(spanContext: SpanContext, attributes: List<KeyValue> = emptyList())

    /**
     * Signals that the operation described by this span has now ended.
     */
    public fun end() {
        endWithTimestamp(Time.now())
    }

    /**
     * Signals that the operation described by this span ended at the given time.
     */
    public fun endWithTimestamp(timestamp: Instant)
}

/**
 * [SpanKind] describes the relationship between the [Span], its parents, and
 * its children in a trace.
 */
public enum class SpanKind {
    /**
     * Indicates that the span describes a request to some remote service.
     */
    Client,

    /**
     * Indicates that the span covers server-side handling of a synchronous RPC
     * or other remote request.
     */
    Server,

    /**
     * Indicates that the span describes the initiators of an asynchronous
     * request.
     */
    Producer,

    /**
     * Indicates that the span describes a child of an asynchronous
     * [SpanKind.Producer] request.
     */
    Consumer,

    /**
     * Indicates that the span represents an internal operation within an
     * application.
     */
    Internal,

    ;

    public companion object {
        public val DEFAULT: SpanKind = Internal
    }
}

/**
 * The status of a [Span].
 *
 * These values form a total order: Ok > Error > Unset.
 */
public sealed class Status : Comparable<Status> {
    /**
     * The default status.
     */
    public data object Unset : Status() {
        override fun compareTo(other: Status): Int =
            when (other) {
                Unset -> 0
                is Error, Ok -> -1
            }
    }

    /**
     * The operation contains an error.
     */
    public data class Error(
        public val description: String,
    ) : Status() {
        override fun compareTo(other: Status): Int =
            when (other) {
                Unset -> 1
                is Error -> 0
                Ok -> -1
            }
    }

    /**
     * The operation has been validated to have completed successfully.
     */
    public data object Ok : Status() {
        override fun compareTo(other: Status): Int =
            when (other) {
                Unset, is Error -> 1
                Ok -> 0
            }
    }

    public companion object {
        public fun error(description: String): Status = Error(description)
    }
}
