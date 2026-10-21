// port-lint: source metrics/instruments/counter.rs
package io.github.kotlinmania.opentelemetry.metrics.instruments

import io.github.kotlinmania.opentelemetry.KeyValue

/**
 * An instrument that records increasing values.
 */
public class Counter<T>(
    private val inner: SyncInstrument<T>,
) {
    public fun add(value: T, attributes: List<KeyValue> = emptyList()) {
        inner.measure(value, attributes)
    }

    public fun add(value: T, vararg attributes: KeyValue) {
        inner.measure(value, attributes.toList())
    }

    public companion object {
        public fun <T> new(inner: SyncInstrument<T>): Counter<T> = Counter(inner)
    }
}

/**
 * An async instrument that records increasing values.
 */
public class ObservableCounter<T> private constructor() {
    public companion object {
        public fun <T> new(): ObservableCounter<T> = ObservableCounter()
    }
}
