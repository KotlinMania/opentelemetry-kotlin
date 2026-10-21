// port-lint: source metrics/instruments/up_down_counter.rs
package io.github.kotlinmania.opentelemetry.metrics.instruments

import io.github.kotlinmania.opentelemetry.KeyValue

/**
 * An instrument that records increasing or decreasing values.
 */
public class UpDownCounter<T>(
    private val inner: SyncInstrument<T>,
) {
    public fun add(value: T, attributes: List<KeyValue> = emptyList()) {
        inner.measure(value, attributes)
    }

    public fun add(value: T, vararg attributes: KeyValue) {
        inner.measure(value, attributes.toList())
    }

    public companion object {
        public fun <T> new(inner: SyncInstrument<T>): UpDownCounter<T> = UpDownCounter(inner)
    }
}

/**
 * An async instrument that records increasing or decreasing values.
 */
public class ObservableUpDownCounter<T> private constructor() {
    public companion object {
        public fun <T> new(): ObservableUpDownCounter<T> = ObservableUpDownCounter()
    }
}
