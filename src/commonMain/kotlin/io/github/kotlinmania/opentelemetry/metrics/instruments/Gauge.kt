// port-lint: source metrics/instruments/gauge.rs
package io.github.kotlinmania.opentelemetry.metrics.instruments

import io.github.kotlinmania.opentelemetry.KeyValue

/**
 * An instrument that records independent values.
 */
public class Gauge<T>(
    private val inner: SyncInstrument<T>,
) {
    public fun record(value: T, attributes: List<KeyValue> = emptyList()) {
        inner.measure(value, attributes)
    }

    public fun record(value: T, vararg attributes: KeyValue) {
        inner.measure(value, attributes.toList())
    }

    public companion object {
        public fun <T> new(inner: SyncInstrument<T>): Gauge<T> = Gauge(inner)
    }
}

/**
 * An async instrument that records independent readings.
 */
public class ObservableGauge<T> private constructor() {
    public companion object {
        public fun <T> new(): ObservableGauge<T> = ObservableGauge()
    }
}
