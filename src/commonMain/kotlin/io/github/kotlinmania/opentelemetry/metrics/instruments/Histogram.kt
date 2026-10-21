// port-lint: source metrics/instruments/histogram.rs
package io.github.kotlinmania.opentelemetry.metrics.instruments

import io.github.kotlinmania.opentelemetry.KeyValue

/**
 * An instrument that records a distribution of values.
 */
public class Histogram<T>(
    private val inner: SyncInstrument<T>,
) {
    public fun record(value: T, attributes: List<KeyValue> = emptyList()) {
        inner.measure(value, attributes)
    }

    public fun record(value: T, vararg attributes: KeyValue) {
        inner.measure(value, attributes.toList())
    }

    public companion object {
        public fun <T> new(inner: SyncInstrument<T>): Histogram<T> = Histogram(inner)
    }
}
