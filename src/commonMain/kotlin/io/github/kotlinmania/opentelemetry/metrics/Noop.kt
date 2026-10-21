// port-lint: source metrics/noop.rs
package io.github.kotlinmania.opentelemetry.metrics

import io.github.kotlinmania.opentelemetry.InstrumentationScope
import io.github.kotlinmania.opentelemetry.KeyValue
import io.github.kotlinmania.opentelemetry.metrics.instruments.SyncInstrument

/**
 * A no-op instance of a [MeterProvider].
 */
public class NoopMeterProvider : MeterProvider {
    override fun meterWithScope(scope: InstrumentationScope): Meter =
        Meter(NoopMeter.new())

    public companion object {
        public fun new(): NoopMeterProvider = NoopMeterProvider()
    }
}

/**
 * A no-op instance of a [Meter].
 */
public class NoopMeter : InstrumentProvider {
    public companion object {
        public fun new(): NoopMeter = NoopMeter()
    }
}

/**
 * A no-op sync instrument.
 */
public class NoopSyncInstrument<T> : SyncInstrument<T> {
    override fun measure(measurement: T, attributes: List<KeyValue>) {
        // Ignored
    }

    public companion object {
        public fun <T> new(): NoopSyncInstrument<T> = NoopSyncInstrument()
    }
}
