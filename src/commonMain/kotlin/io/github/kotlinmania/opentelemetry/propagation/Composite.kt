// port-lint: source propagation/composite.rs
package io.github.kotlinmania.opentelemetry.propagation

import io.github.kotlinmania.opentelemetry.Context

/**
 * Composite propagator for [TextMapPropagator]s.
 *
 * A propagator that chains multiple [TextMapPropagator] propagators together,
 * injecting or extracting by their respective HTTP header names.
 *
 * Injection and extraction from this propagator will preserve the order of the
 * injectors and extractors passed in during initialization.
 */
public class TextMapCompositePropagator(
    private val propagators: List<TextMapPropagator>,
) : TextMapPropagator {
    private val fieldsList: List<String>

    init {
        val set = LinkedHashSet<String>()
        for (propagator in propagators) {
            val iter = propagator.fields()
            while (iter.hasNext()) {
                set.add(iter.next())
            }
        }
        fieldsList = set.toList()
    }

    public fun propagators(): List<TextMapPropagator> = propagators

    /**
     * Encodes the values of the [Context] and injects them into the [Injector].
     */
    override fun injectContext(cx: Context, injector: Injector) {
        for (propagator in propagators) {
            propagator.injectContext(cx, injector)
        }
    }

    /**
     * Retrieves encoded [Context] information using the [Extractor]. If no data was
     * retrieved OR if the retrieved data is invalid, then the current [Context] is
     * returned.
     */
    override fun extractWithContext(cx: Context, extractor: Extractor): Context {
        var currentCx = cx
        for (propagator in propagators) {
            currentCx = propagator.extractWithContext(currentCx, extractor)
        }
        return currentCx
    }

    override fun fields(): FieldIter = FieldIter.new(fieldsList)

    public companion object {
        public fun new(propagators: List<TextMapPropagator>): TextMapCompositePropagator =
            TextMapCompositePropagator(propagators)
    }
}
