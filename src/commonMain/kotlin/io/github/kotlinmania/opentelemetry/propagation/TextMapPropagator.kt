// port-lint: source propagation/text_map_propagator.rs
package io.github.kotlinmania.opentelemetry.propagation

import io.github.kotlinmania.opentelemetry.Context

/**
 * Methods to inject and extract a value as text into injectors and extractors that travel
 * in-band across process boundaries.
 */
public interface TextMapPropagator {
    /**
     * Properly encodes the values of the current [Context] and injects them into
     * the [Injector].
     */
    public fun inject(injector: Injector) {
        Context.mapCurrent { cx -> injectContext(cx, injector) }
    }

    /**
     * Properly encodes the values of the [Context] and injects them into the
     * [Injector].
     */
    public fun injectContext(cx: Context, injector: Injector)

    /**
     * Retrieves encoded data using the provided [Extractor]. If no data for this
     * format was retrieved OR if the retrieved data is invalid, then the current
     * [Context] is returned.
     */
    public fun extract(extractor: Extractor): Context =
        Context.mapCurrent { cx -> extractWithContext(cx, extractor) }

    /**
     * Retrieves encoded data using the provided [Extractor]. If no data for this
     * format was retrieved OR if the retrieved data is invalid, then the given
     * [Context] is returned.
     */
    public fun extractWithContext(cx: Context, extractor: Extractor): Context

    /**
     * Returns iter of fields used by [TextMapPropagator].
     */
    public fun fields(): FieldIter
}

/**
 * An iterator over fields of a [TextMapPropagator].
 */
public class FieldIter(
    private val fields: List<String>,
) : Iterator<String> {
    private var index: Int = 0

    override fun hasNext(): Boolean = index < fields.size

    override fun next(): String {
        if (!hasNext()) throw NoSuchElementException("No more fields")
        return fields[index++]
    }

    public fun toList(): List<String> = fields

    public companion object {
        public typealias Item = String

        public fun new(fields: List<String>): FieldIter = FieldIter(fields)
    }
}
