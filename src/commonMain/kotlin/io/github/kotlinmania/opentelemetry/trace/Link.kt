package io.github.kotlinmania.opentelemetry.trace

import io.github.kotlinmania.opentelemetry.KeyValue

/**
 * Link is the relationship between two Spans.
 *
 * The relationship can be within the same trace or across different traces.
 */
public data class Link(
    /**
     * The span context of the linked span.
     */
    public val spanContext: SpanContext,
    /**
     * Attributes that describe this link.
     */
    public val attributes: List<KeyValue> = emptyList(),
    /**
     * The number of attributes that were above the configured limit, and thus dropped.
     */
    public val droppedAttributesCount: UInt = 0u,
) {
    public companion object {
        /**
         * Create new [Link].
         */
        public fun new(
            spanContext: SpanContext,
            attributes: List<KeyValue>,
            droppedAttributesCount: UInt = 0u,
        ): Link = Link(spanContext, attributes, droppedAttributesCount)

        /**
         * Create new [Link] with given context.
         */
        public fun withContext(spanContext: SpanContext): Link =
            Link(
                spanContext = spanContext,
                attributes = emptyList(),
                droppedAttributesCount = 0u,
            )
    }
}
