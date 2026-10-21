package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.time.Time
import kotlin.time.Instant

/**
 * Events record things that happened during a [Span]'s lifetime.
 */
public data class Event(
    /**
     * The name of this event.
     */
    public val name: String,
    /**
     * The time at which this event occurred.
     */
    public val timestamp: Instant,
    /**
     * Attributes that describe this event.
     */
    public val attributes: List<KeyValue> = emptyList(),
    /**
     * The number of attributes that were above the configured limit, and thus dropped.
     */
    public val droppedAttributesCount: UInt = 0u,
) {
    public companion object {
        /**
         * Create new [Event].
         */
        public fun new(
            name: String,
            timestamp: Instant,
            attributes: List<KeyValue>,
            droppedAttributesCount: UInt = 0u,
        ): Event = Event(name, timestamp, attributes, droppedAttributesCount)

        /**
         * Create new [Event] with a given name.
         */
        public fun withName(name: String): Event =
            Event(
                name = name,
                timestamp = Time.now(),
                attributes = emptyList(),
                droppedAttributesCount = 0u,
            )
    }
}
